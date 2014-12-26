import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.dgso.programbuilder.ProgramBuilder;
import org.dgso.testrunner.TestBuilder;
import org.dgso.testrunner.TestBuilderFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class Main {
    private static Logger mainLogger = Logger.getLogger(Main.class);

    private static String grammar_path;
    private static int recursion_limit;
    private static String startingRule;
    private static String templateFolder;
    private static String templateFile;
    private static int instanceCount;

    public static void main(String[] args) {
        String inputFile;

        // setup logger
        BasicConfigurator.configure();

        // create a CharStream that reads from standard input
        if (args.length != 1) {
            mainLogger.error("dgso requires 1 argument: the path to the configuration file.");
            System.exit(-1);
        }

        inputFile = args[0];

        try {
            setupParameters(inputFile);

            ArrayList<String> statements = ProgramBuilder.getAllStatementsFromGrammar(grammar_path, startingRule, recursion_limit);

            TestBuilderFactory.createTestBuilders(templateFolder, templateFile, instanceCount);

            TestBuilder tb = TestBuilderFactory.getTestBuilder(1);

            //test code
            tb.buildProgram("asdfasdfasdfasdf");
            System.out.println(statements);
        } catch (IOException e) {
            mainLogger.error(e.getMessage());
        }
    }

    public static void setupParameters(String pathToConfigFile) throws IOException {
        Properties properties = new Properties();
        InputStream is = new FileInputStream(pathToConfigFile);
        properties.load(is);

        grammar_path = properties.getProperty("grammar_path");
        recursion_limit = Integer.parseInt(properties.getProperty("program_builder_recursion_limit"));
        startingRule = properties.getProperty("starting_rule");
        templateFolder = properties.getProperty("template_folder");
        templateFile = properties.getProperty("template_file");
        instanceCount = Integer.parseInt(properties.getProperty("instance_count"));
    }
}
