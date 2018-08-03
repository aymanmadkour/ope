package ope;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ope.fast.FastOpeTest;
import ope.mope.MopeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FastOpeTest.class, MopeTest.class })
public class TestSuite {}
