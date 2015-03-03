package ltl2rabin;

import ltl2rabin.parser.LTLBaseListener;
import ltl2rabin.parser.LTLParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class LTLListener extends LTLBaseListener {
    LTLFormula LTLTree;
    LTLParser parser;

    public LTLListener(LTLParser parser) {
        this.parser = parser;
    }

    @Override
    public void enterFormulaf(LTLParser.FormulafContext ctx) {
        super.enterFormulaf(ctx);
    }

    @Override
    public void exitFormulaf(LTLParser.FormulafContext ctx) {
        if (null == ctx.getParent()) {
            System.out.println("This F right here is the parent node.");
            System.out.println(ctx.getText());

        }
        super.exitFormulaf(ctx);
    }

    @Override
    public void enterFormulag(LTLParser.FormulagContext ctx) {
        if (null == ctx.getParent()) {
            System.out.println("This G right here is the parent node.");
        }
        super.enterFormulag(ctx);
    }

    @Override
    public void exitFormulag(LTLParser.FormulagContext ctx) {
        super.exitFormulag(ctx);
    }

    @Override
    public void enterFormulax(LTLParser.FormulaxContext ctx) {
        if (null == ctx.getParent()) {
            System.out.println("This X right here is the parent node.");
        }
        super.enterFormulax(ctx);
    }

    @Override
    public void exitFormulax(LTLParser.FormulaxContext ctx) {
        super.exitFormulax(ctx);
    }

    @Override
    public void enterFormulaorformula(LTLParser.FormulaorformulaContext ctx) {
        super.enterFormulaorformula(ctx);
    }

    @Override
    public void exitFormulaorformula(LTLParser.FormulaorformulaContext ctx) {
        super.exitFormulaorformula(ctx);
    }

    @Override
    public void enterFormulaatom(LTLParser.FormulaatomContext ctx) {
        super.enterFormulaatom(ctx);
    }

    @Override
    public void exitFormulaatom(LTLParser.FormulaatomContext ctx) {
        super.exitFormulaatom(ctx);
    }

    @Override
    public void enterFormulainparentheses(LTLParser.FormulainparenthesesContext ctx) {
        super.enterFormulainparentheses(ctx);
    }

    @Override
    public void exitFormulainparentheses(LTLParser.FormulainparenthesesContext ctx) {
        super.exitFormulainparentheses(ctx);
    }

    @Override
    public void enterOrformula(LTLParser.OrformulaContext ctx) {
        super.enterOrformula(ctx);
    }

    @Override
    public void exitOrformula(LTLParser.OrformulaContext ctx) {
        super.exitOrformula(ctx);
    }

    @Override
    public void enterAndformula(LTLParser.AndformulaContext ctx) {
        super.enterAndformula(ctx);
    }

    @Override
    public void exitAndformula(LTLParser.AndformulaContext ctx) {
        super.exitAndformula(ctx);
    }

    @Override
    public void enterUformula(LTLParser.UformulaContext ctx) {
        super.enterUformula(ctx);
    }

    @Override
    public void exitUformula(LTLParser.UformulaContext ctx) {
        super.exitUformula(ctx);
    }

    @Override
    public void enterAtom(LTLParser.AtomContext ctx) {
        super.enterAtom(ctx);
    }

    @Override
    public void exitAtom(LTLParser.AtomContext ctx) {
        if(null != ctx.Identifier()) {
            boolean negated = false;
            String name = ctx.Identifier().getText();
            if (name.startsWith("!")) {
                negated = true;
                name = name.substring(1);
            }
            LTLVariable var = new LTLVariable(name, negated);
            System.out.println("Created variable var=" + var.toString());
        }
        if (null != ctx.Boolean()) {
            // Create boolean here
        }
        super.exitAtom(ctx);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        super.enterEveryRule(ctx);
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        super.exitEveryRule(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        super.visitTerminal(node);
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        super.visitErrorNode(node);
    }
}
