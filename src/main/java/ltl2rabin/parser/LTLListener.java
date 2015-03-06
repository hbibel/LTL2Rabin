package ltl2rabin.parser;
// Generated from LTL.g4 by ANTLR 4.5
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LTLParser}.
 */
public interface LTLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code formulaf}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormulaf(LTLParser.FormulafContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formulaf}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormulaf(LTLParser.FormulafContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formulag}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormulag(LTLParser.FormulagContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formulag}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormulag(LTLParser.FormulagContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formulax}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormulax(LTLParser.FormulaxContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formulax}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormulax(LTLParser.FormulaxContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formulaorformula}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormulaorformula(LTLParser.FormulaorformulaContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formulaorformula}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormulaorformula(LTLParser.FormulaorformulaContext ctx);
	/**
	 * Enter a parse tree produced by the {@code formulaatom}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormulaatom(LTLParser.FormulaatomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code formulaatom}
	 * labeled alternative in {@link LTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormulaatom(LTLParser.FormulaatomContext ctx);
	/**
	 * Enter a parse tree produced by {@link LTLParser#formulainparentheses}.
	 * @param ctx the parse tree
	 */
	void enterFormulainparentheses(LTLParser.FormulainparenthesesContext ctx);
	/**
	 * Exit a parse tree produced by {@link LTLParser#formulainparentheses}.
	 * @param ctx the parse tree
	 */
	void exitFormulainparentheses(LTLParser.FormulainparenthesesContext ctx);
	/**
	 * Enter a parse tree produced by {@link LTLParser#orformula}.
	 * @param ctx the parse tree
	 */
	void enterOrformula(LTLParser.OrformulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LTLParser#orformula}.
	 * @param ctx the parse tree
	 */
	void exitOrformula(LTLParser.OrformulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LTLParser#andformula}.
	 * @param ctx the parse tree
	 */
	void enterAndformula(LTLParser.AndformulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LTLParser#andformula}.
	 * @param ctx the parse tree
	 */
	void exitAndformula(LTLParser.AndformulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LTLParser#uformula}.
	 * @param ctx the parse tree
	 */
	void enterUformula(LTLParser.UformulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LTLParser#uformula}.
	 * @param ctx the parse tree
	 */
	void exitUformula(LTLParser.UformulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link LTLParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(LTLParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link LTLParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(LTLParser.AtomContext ctx);
}
