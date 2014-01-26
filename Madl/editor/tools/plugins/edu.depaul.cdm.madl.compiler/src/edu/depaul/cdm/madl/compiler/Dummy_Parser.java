package edu.depaul.cdm.madl.compiler;

public class Dummy_Parser {
	// prevent from creating instances
	public Dummy_Parser() {
	}

	private static String[] keywords = { "app", "abstract", "as", "assert", "boolean",
			"break", "byte", "case", "catch", "char", "class", "const",
			"continue", "def", "default", "do", "double", "else", "enum",
			"extends", "false", "final", "finally", "float", "for", "goto",
			"if", "implements", "import", "in", "instanceof", "int",
			"interface", "long", "native", "new", "null", "package", "private",
			"protected", "public", "return", "short", "static", "strictfp",
			"super", "switch", "synchronized", "this", "threadsafe", "throw",
			"throws", "transient", "true", "try", "void", "volatile", "while" };

	public static String[] getKeyWords() {
		return keywords;
	}
}
