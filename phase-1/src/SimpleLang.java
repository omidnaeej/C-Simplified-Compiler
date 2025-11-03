import main.ast.nodes.Program;
import main.grammar.SimpleLangLexer;
import main.grammar.SimpleLangParser;
import main.visitor.MyASTVisitor;
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

        MyASTVisitor myVisitor = new MyASTVisitor();
        myVisitor.visit(program);
    }
}