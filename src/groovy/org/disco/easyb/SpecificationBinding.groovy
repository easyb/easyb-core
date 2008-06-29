package org.disco.easyb

import org.disco.easyb.listener.ExecutionListener
import org.disco.easyb.result.Result

class SpecificationBinding extends Binding {
    SpecificationKeywords specification;

    def SpecificationBinding(ExecutionListener listener) {
        specification = new SpecificationKeywords(listener)

        def pendingClosure = {
            listener.gotResult(new Result(Result.PENDING))
        }

        before = {description, closure = {} ->
            specification.before(description, closure)
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
    static Binding getBinding(listener) {
        return new SpecificationBinding(listener)
    }
}