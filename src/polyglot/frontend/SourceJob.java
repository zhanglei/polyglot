package jltools.frontend;

import jltools.ast.*;
import jltools.parse.*;
import jltools.lex.*;
import jltools.types.*;
import jltools.util.*;
import jltools.visit.*;
import jltools.main.Main;

import java.io.*;
import java.util.*;

/**
 * A <code>SourceJob</code> encapsulates work done by the compiler on behalf
 * of a source file target.  It includes all information carried between
 * phases of the compiler.
 */
public class SourceJob extends Job
{
  Node ast;
  TableClassResolver parsedResolver; 
  ClassResolver systemResolver; 
  ErrorQueue eq;

  public SourceJob(Target t,
		   ErrorQueue eq,
		   ClassResolver systemResolver,
		   TableClassResolver parsedResolver)
  {
    super(t);
    this.eq = eq;
    this.systemResolver = systemResolver;
    this.parsedResolver = parsedResolver;
  }

  public Node getAST() {
    return ast;
  }

  public void parse() {
    ExtensionInfo extInfo = Compiler.getExtensionInfo();
    java_cup.runtime.Symbol sym = null;

    try {
      Reader reader = t.getSourceReader();
      java_cup.runtime.lr_parser grm = extInfo.getParser(reader, eq);
      sym = grm.parse();
    }
    catch (IOException e) {
      eq.enqueue( ErrorInfo.IO_ERROR, e.getMessage());
      return;
    }
    catch (Exception e) {
      e.printStackTrace();
      eq.enqueue( ErrorInfo.INTERNAL_ERROR, e.getMessage());
      return;
    }

    try {
      // done with the input
      t.closeSource();
    }
    catch (IOException e) {
      eq.enqueue( ErrorInfo.IO_ERROR, e.getMessage());
      return;
    }

    /* Try and figure out whether or not the parser was successful. */
    if (sym == null) {
      eq.enqueue( ErrorInfo.SYNTAX_ERROR, "Unable to parse source file.");
      return;
    }

    if (! (sym.value instanceof Node)) {
      eq.enqueue( ErrorInfo.SYNTAX_ERROR, "Unable to parse source file.");
      return;
    }

    ast = (Node) sym.value;

    runVisitors(PARSED);
  }

  public void read() {
    it = new ImportTable(systemResolver, true, eq);
    runVisitors(READ);
    if (eq.hasErrors()) {
      return;
    }
    parsedResolver.include(cr);
  }

  public void clean() {
    runVisitors(CLEANED);
  }
  public void disambiguate() {
    runVisitors(DISAMBIGUATED);
  }
  public void check() {
    runVisitors(CHECKED);
  }
  public void translate() {
    runVisitors(TRANSLATED);
    ast = null;
  }

  private void runVisitors(int stage) {
    NodeVisitor v;
    Node result = ast;

    ExtensionInfo extInfo = Compiler.getExtensionInfo();

    for (Iterator iter = extInfo.getNodeVisitors(this, stage).iterator();
         iter.hasNext(); ) {

      v = (NodeVisitor) iter.next();

      Compiler.verbose( this,
	"running visitor " + v.getClass().getName() + "...");

      result = result.visit( v);
      v.finish();
    }

    ast = result;

    if (eq.hasErrors()) {
      return;
    }

    status |= stage;
  }

  public void dump(CodeWriter w) throws IOException {
    if (ast != null) {
      DumpAst d = new DumpAst(w);
      ast.visit(d);
    }
    w.flush();
  }
}
