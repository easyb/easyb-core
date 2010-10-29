package org.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.result.Result
import org.easyb.plugin.PluginLocator
import org.easyb.plugin.SyntaxExtension

class SpecificationBinding extends Binding {
  SpecificationKeywords specification;
  File specParentDir
  SpecificationBinding self

  def addSyntax(SyntaxExtension ext) {
    ext.getSyntax()?.each { methodName, closure ->
      self."$methodName" = { Object[] params ->
        specification.extensionMethod(closure, params, self)
      }
    }

    // add extra extension categories (if any)
    ext.getExtensionCategories()?.each { cat ->
      specification.categories.add(cat)
    }
  }

  def SpecificationBinding(ExecutionListener listener, File specParentDir) {
    specification = new SpecificationKeywords(listener)
    this.specParentDir = specParentDir
    self = this

    // load all auto syntax adding plugins
    PluginLocator.findAllAutoloadingSyntaxExtensions().each { SyntaxExtension ext ->
      addSyntax(ext)
    }

    def pendingClosure = {
      listener.gotResult(new Result(Result.PENDING))
    }

    before = {description, closure = {} ->
      specification.before(description, closure)
    }

    after = {description, closure = {} ->
      specification.after(description, closure)
    }

    shared_specs = { file ->
      if (!specParentDir)
        specParentDir = new File(".")

      if (file.indexOf('.') < 0)
        file += ".shared"

      GroovyShell g = new GroovyShell(self.getClass().getClassLoader(), self);

      File specFile = new File(specParentDir, file);

      g.evaluate(specFile);

    }

    using = {pluginName, pluginVariableName = null ->
      def plugin = new PluginLocator().findPluginWithName(pluginName)

      if (pluginVariableName) {
        setProperty(pluginVariableName, plugin)
      }
    }

    extension = { name ->
      addSyntax(PluginLocator.findSyntaxExtensionByName(name))
    }


    it = {spec, closure = pendingClosure ->
      specification.it(spec, closure)
    }

    and = {
      specification.and()
    }

    narrative = {description = "", closure = {} ->
      specification.narrative(description, closure)
    }

    description = {description ->
      specification.description(description)
    }
  }

  /**
   * This method returns a fully initialized Binding object (or context) that
   * has definitions for methods such as "it" and "given", which are used
   * in the context of behaviors (or stories).
   */
  static Binding getBinding(ExecutionListener listener, File specParentDir) {
    return new SpecificationBinding(listener, specParentDir)
  }

  static Binding getBinding(ExecutionListener listener) {
    return getBinding(listener, null)
  }

}