package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;
import rs.etf.pp1.symboltable.visitors.SymbolTableVisitor;


public class Compiler {
	
	

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void dump(SymbolTableVisitor stv) {
		System.out.println("=====================SADRZAJ TABELE SIMBOLA=========================");
		if (stv == null)
			stv = new MyDumpSymbolTable();
		for (Scope s = MyTab.currentScope; s != null; s = s.getOuter()) {
			s.accept(stv);
		}
		System.out.println(stv.getOutput());
	}
	
	/** Stampa sadrzaj tabele simbola. */
	public static void tsdump() {
		dump(null);
	}
	
	public static void main(String[] args) throws Exception {
		
		Logger log = Logger.getLogger(Compiler.class);
		
		Reader br = null;
		try {
			File sourceCode = new File("test/program.mj");
			log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
			br = new BufferedReader(new FileReader(sourceCode));
			Yylex lexer = new Yylex(br);
			
			MJParser p = new MJParser(lexer);
	        Symbol s = p.parse();  //pocetak parsiranja
	        
	        if(!p.errorDetected) {
	        	MyTab.init();
		        Program prog = (Program)(s.value); 

				// ispis sintaksnog stabla
				log.info(prog.toString(""));
				System.out.println("=====================SEMANTICKA ANALIZA=========================");

				// ispis prepoznatih programskih konstrukcija
				SemanticAnalyzer v = new SemanticAnalyzer();
				prog.traverseBottomUp(v); 
				System.out.println("=====================SINTAKSNA ANALIZA=========================");
				log.info(" Print count calls = " + v.printCallCount);

				log.info(" Deklarisanih promenljivih ima = " + v.varDeclCount);
				tsdump();
				System.out.println("=====================REZULTAT=========================");
				if(v.passed()) {
					
					File objFile = new File("test/program.obj");
					if(objFile.exists()) objFile.delete();
					
					CodeGenerator codeGenerator = new CodeGenerator();
					prog.traverseBottomUp(codeGenerator);
					Code.dataSize = v.nVars;
					Code.mainPc = codeGenerator.getMainPc();
					Code.write(new FileOutputStream(objFile));
					
					log.info("Uspesna semanticka analiza!!");
				}else {
					log.info("Neuspesna semanticka analiza!!");
				}
				
	        }else {
	        	log.info("Neuspesna sintaksna analiza!!");
	        }
	        
	        
			
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { log.error(e1.getMessage(), e1); }
		}

	}
	
	
}
