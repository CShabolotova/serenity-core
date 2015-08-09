package net.serenitybdd.journey

import net.serenitybdd.journey.shopping.DanaGoesShopping
import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.model.TestResult
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

import static net.serenitybdd.journey.TestOutcomeChecks.resultsFrom
import static net.thucydides.core.model.TestResult.ERROR
import static net.thucydides.core.model.TestResult.FAILURE
import static net.thucydides.core.model.TestResult.SKIPPED

class WhenRunningASimpleJourney extends Specification{

    def "should produce a normal test outcome"() {
        given:
            def runner = new SerenityRunner(DanaGoesShopping)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            !results.empty
        and:
            def outcome = results["shouldBeAbleToPurchaseSomeItems"]
            outcome.title == "Should be able to purchase some items"
            outcome.result == TestResult.SUCCESS
    }

    def "should produce a step for each task call"() {
        given:
            def runner = new SerenityRunner(DanaGoesShopping)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseSomeItems"]
            outcome.testSteps.collect { it.unrendered().description } == ["Given Dana has purchased an apple for 10 dollars",
                                                                          "Given Dana has purchased a pear for 5 dollars"]
    }

    def "should produce a failed step when an assumption fails"() {
        given:
            def runner = new SerenityRunner(DanaGoesShopping)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseAnItemForFree"]
            outcome.result == FAILURE
            outcome.testSteps.collect { it.unrendered().description } == ["Given Dana has purchased an apple for 0 dollars",
                                                                          "Given Dana has purchased a pear for 5 dollars"]
            outcome.testSteps.collect { it.result } == [FAILURE, SKIPPED]
    }

    def "should produce a step with an error when a step breaks"() {
        given:
            def runner = new SerenityRunner(DanaGoesShopping)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseAnItemWithANegativeAmount"]
            outcome.result == ERROR
            outcome.testSteps.collect { it.result } == [ERROR, SKIPPED]
    }

    def "should document tasks as far as possible when errors occur"() {
        given:
            def runner = new SerenityRunner(DanaGoesShopping)
        when:
            runner.run(new RunNotifier())
            def results = resultsFrom(runner.testOutcomes)
        then:
            def outcome = results["shouldBeAbleToPurchaseAnItemWithANegativeAmount"]
            outcome.testSteps.collect { it.unrendered().description } == ["Given Dana has purchased an apple for -10 dollars",
                                                                          "Given Dana has purchased a pear for 5 dollars"]
    }

}