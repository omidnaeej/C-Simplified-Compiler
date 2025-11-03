import main.ast.nodes.Program;
import main.grammar.SimpleLangLexer;
import main.grammar.SimpleLangParser;
import main.symbolTable.SymbolTable;
import main.visitor.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.IOException;

public class SimpleLang {
    public static void main(String[] args) throws IOException {
        CharStream reader = CharStreams.fromFileName(args[0]);
        SimpleLangLexer simpleLangLexer = new SimpleLangLexer(reader);
        CommonTokenStream tokens = new CommonTokenStream(simpleLangLexer);
        SimpleLangParser flParser = new SimpleLangParser(tokens);
        Program program = flParser.program().programRet;
        System.out.println();

        MyNameAnalyzer myNameAnalyzer = new MyNameAnalyzer();
        myNameAnalyzer.visit(program);

        if (! myNameAnalyzer.isDetectError()) {
            ReturnEliminationVisitor returnEliminationVisitor = new ReturnEliminationVisitor();
            returnEliminationVisitor.visit(program);

            ReachabilityAnalysisVisitor reachabilityAnalysisVisitor = new ReachabilityAnalysisVisitor(program);
            reachabilityAnalysisVisitor.run();

            EffectFreeEliminationVisitor effectFreeEliminationVisitor = new EffectFreeEliminationVisitor();
            effectFreeEliminationVisitor.visit(program);

            MyASTVisitor myASTVisitor = new MyASTVisitor();
            myASTVisitor.visit(program);
        }


        SymbolTable.root.hashCode();
        System.out.println();
    }
}