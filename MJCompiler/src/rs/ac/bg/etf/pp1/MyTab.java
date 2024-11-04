package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class MyTab extends Tab {
	
	public static final Struct boolType = new Struct(Struct.Bool);
	
	public static void init() {
		
		Tab.init();
		currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
		currentScope.addToLocals(new Obj(Obj.Con, "true", intType, 1, 0));
		currentScope.addToLocals(new Obj(Obj.Con, "false", intType, 0, 0));
		
	}

}
