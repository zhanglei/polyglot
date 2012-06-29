package polyglot.ext.jl5.translate;

import polyglot.ast.Ext;
import polyglot.ext.jl5.ast.JL5CanonicalTypeNode_c;

public class JL5ToJLExtFactory_c extends JL5ToExtFactory_c {
	
	@Override
	protected Ext extClassDeclImpl() {
		return new JL5ClassDeclToExt_c();
	}
	
	@Override
	protected Ext extCanonicalTypeNodeImpl() {
		return new JL5TypeNodeToExt_c();
	}
	
	@Override
	protected Ext extParamTypeNodeImpl() {
		return new JL5TypeNodeToExt_c();
	}

	// The below nodes should have been removed 
	// by the time the ExtensionRewriter is called.
	@Override
	protected Ext extEnumDeclImpl() {
		return new CannotToExt_c();
	}

	@Override
	protected Ext extExtendedForImpl() {
		return new CannotToExt_c();
	}

	@Override
	protected Ext extEnumConstantDeclImpl() {
		return new CannotToExt_c();
	}

	@Override
	protected Ext extEnumConstantImpl() {
		return new CannotToExt_c();
	}
}