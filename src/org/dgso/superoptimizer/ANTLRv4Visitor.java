package org.dgso.superoptimizer;

import org.antlr.v4.runtime.misc.NotNull;
import org.apache.log4j.Logger;
import org.dgso.antlrv4parser.ANTLRv4Parser;
import org.dgso.antlrv4parser.ANTLRv4ParserBaseVisitor;

/**
 * ANTLRv4 visitor parser to handle ANTLR text.
 */
public class ANTLRv4Visitor extends ANTLRv4ParserBaseVisitor {

    private Grammar grammar;
    private GrammarClass currentGrammarSpec, currentParserRuleSpec, currentlabeledAlt, currentAltList;
    private Logger logger;

    public ANTLRv4Visitor() {
        logger = Logger.getLogger(ANTLRv4Visitor.class);
        grammar = new Grammar();
        currentGrammarSpec = null;
        currentParserRuleSpec = null;
        currentlabeledAlt = null;
        currentAltList = null;
    }

    /**
     * Visit a parse tree produced by {@link org.dgso.antlrv4parser.ANTLRv4Parser#grammarSpec}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    public Grammar visitGrammarSpec(@NotNull ANTLRv4Parser.GrammarSpecContext ctx) {
        String currentGrammarSpecName = ctx.id().getText();

        logger.debug("* ANTLRv4 Grammar Spec detected");
        logger.debug("* ANTLRv4 Grammar Spec text: " + currentGrammarSpecName);

        currentGrammarSpec = new GrammarClass(currentGrammarSpecName, null, GrammarType.ANTLR_GRAMMAR_SPEC);
        grammar.addEntry(GrammarType.ANTLR_GRAMMAR_SPEC, currentGrammarSpec);

        visitChildren(ctx);

        return grammar;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Grammar visitParserRuleSpec(@NotNull ANTLRv4Parser.ParserRuleSpecContext ctx) {
        String currentParserRuleSpecName = ctx.getStart().getText();

        logger.debug("**** ANTLRv4 Parser Rule Spec detected");
        logger.debug("**** ANTLRv4 Parser Rule Spec text: " + ctx.getText());
        logger.debug("**** ANTLRv4 Parser Rule Spec RHS: " + ctx.ruleBlock().getText());
        logger.debug("**** ANTLRv4 Parser Rule Spec LHS: " + ctx.getStart().getText());

        currentParserRuleSpec = new GrammarClass(currentParserRuleSpecName, currentGrammarSpec, GrammarType.PARSER_RULE_SPEC);
        currentGrammarSpec.addChild(currentParserRuleSpec);
        grammar.addEntry(GrammarType.PARSER_RULE_SPEC, currentParserRuleSpec);

        visitChildren(ctx);

        return grammar;
    }


    /**
     * Visit a parse tree produced by {@link ANTLRv4Parser#labeledAlt}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    public Grammar visitLabeledAlt(@NotNull ANTLRv4Parser.LabeledAltContext ctx) {
        String labeledAltName = ctx.getText();
        currentAltList = null;

        logger.debug("******* ANTLRv4 LabeledAlt detected");
        logger.debug("******* ANTLRv4 LabeledAlt text: " + labeledAltName);

        currentlabeledAlt = new GrammarClass(labeledAltName, currentParserRuleSpec, GrammarType.LABELED_ALT);
        currentParserRuleSpec.addChild(currentlabeledAlt);
        grammar.addEntry(GrammarType.LABELED_ALT, currentParserRuleSpec);

        System.out.println(ctx.alternative());
        visitChildren(ctx);

        return grammar;
    }

    /**
     * Visit a parse tree produced by {@link ANTLRv4Parser#altList}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    public Grammar visitAltList(@NotNull ANTLRv4Parser.AltListContext ctx) {
        String altListName = ctx.getText();

        logger.debug("****** ANTLRv4 AltList detected");
        logger.debug("****** ANTLRv4 AltList text: " + altListName);

        currentAltList = new GrammarClass(altListName, currentParserRuleSpec, GrammarType.ALT_LIST);
        currentlabeledAlt.addChild(currentAltList);
        grammar.addEntry(GrammarType.ALT_LIST, currentAltList);

        visitChildren(ctx);

        return grammar;
    }


    /**
     * {@inheritDoc}
     * <p/>
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public Grammar visitRuleref(@NotNull ANTLRv4Parser.RulerefContext ctx) {
        String ruleRefName = ctx.getText();

        logger.debug("****** ANTLRv4 Rule Reference detected");
        logger.debug("****** ANTLRv4 Rule Reference text: " + ruleRefName);

        GrammarClass grammarSpec = new GrammarClass(ruleRefName, currentParserRuleSpec, GrammarType.RULE_REFERENCE);
        currentlabeledAlt.addChild(grammarSpec);
        grammar.addEntry(GrammarType.RULE_REFERENCE, grammarSpec);

        visitChildren(ctx);

        return grammar;
    }

    /**
     * Visit a parse tree produced by {@link ANTLRv4Parser#terminal}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    public Grammar visitTerminal(@NotNull ANTLRv4Parser.TerminalContext ctx) {
        String atomName = ctx.getText();

        logger.debug("******* ANTLRv4 Terminal detected");
        logger.debug("******* ANTLRv4 Terminal text: " + atomName);

        GrammarClass grammarSpec;

        if (currentAltList != null) {
            grammarSpec = new GrammarClass(atomName, currentAltList, GrammarType.RULE_TERMINAL);
            currentAltList.addChild(grammarSpec);
        } else {
            grammarSpec = new GrammarClass(atomName, currentlabeledAlt, GrammarType.RULE_TERMINAL);
            currentlabeledAlt.addChild(grammarSpec);
        }
        grammar.addEntry(GrammarType.RULE_TERMINAL, grammarSpec);


        visitChildren(ctx);

        return grammar;
    }
}
