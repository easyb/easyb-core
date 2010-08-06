/*
 * Created by IntelliJ IDEA.
 * User: richard
 * Date: Aug 3, 2010
 * Time: 8:24:13 PM
 */
package org.easyb.transforms

import org.junit.Test
import org.codehaus.groovy.control.CompilePhase
import org.easyb.delegates.ShouldTransformation
import org.codehaus.groovy.tools.ast.TranformTestHelper;
public class TransformerTest {
  public void shouldDoThis() {

  }

  @Test
  public void test() {
    println (new File(".").absoluteFile)
    def file = new File('behavior/groovy/org/easyb/bdd/issues/Issue87.story')
    assert file.exists()


    def invoker = new TranformTestHelper(new ShouldTransformation(), CompilePhase.CANONICALIZATION)

    def clazz = invoker.parse(file)
    def tester = clazz.newInstance()
    
  }
}