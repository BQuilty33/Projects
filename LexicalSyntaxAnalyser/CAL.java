import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.CharStreams;
import java.io.FileInputStream;
import java.io.InputStream;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class CAL
{

	public static void main (String[] args) throws Exception
	{
		String inputFile = null;
		if (args.length > 0)
			inputFile = args[0];
			
		InputStream is = System.in;
		if (inputFile != null){
			is = new FileInputStream (inputFile);
			calLexer lexer = new calLexer (CharStreams.fromStream (is));
			lexer.removeErrorListeners();
			CommonTokenStream tokens = new CommonTokenStream (lexer);
			calParser parser = new calParser (tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(new BaseErrorListener(){
				public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) throws ParseCancellationException {
					throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
				}
			}
			);
			try{
				ParseTree tree = parser.prog ();
				System.out.println(inputFile + " parsed successfully");
			}
			catch (Exception e) {
				System.out.println(inputFile + " has not parsed" + e);
			}
		}
	}

}