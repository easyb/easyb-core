package org.easyb.util;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import org.easyb.BehaviorStep;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * decodes a string passed given a binding. #{} is decoded as a GroovyShell, # is just grabbed as a property.
 * 
 */
public class TextDecoder {
  private static final Pattern VARIABLE_PATTERN = Pattern.compile("((#\\{(.*?)\\})|(#([a-zA-Z_\\$][\\w\\$]*)))");
  private final Matcher variableMatcher;

  public TextDecoder(String text) {
    variableMatcher = VARIABLE_PATTERN.matcher(text);
  }

  public String replace(Binding binding, BehaviorStep parentStep) {
    StringBuffer sb = new StringBuffer(); // variableMatcher needs this instead of Builder

    GroovyShell shell = null;

    variableMatcher.reset();

    while (variableMatcher.find()) {
      String variableName = variableMatcher.group(1);

      Object prop;

      if (variableName.startsWith("#{")) {
        if ( shell == null )
          shell = new GroovyShell(binding);

        prop = evalValue(binding, variableName, shell);
      } else
        prop = getValue(binding, variableName, parentStep);


      
      String value = ( prop == null ) ? variableName : prop.toString();

      variableMatcher.appendReplacement(sb, value);
    }

    variableMatcher.appendTail(sb);

    return sb.toString();
  }

  private Object evalValue(Binding binding, String script, GroovyShell shell) {
    if ( script != null && script.length() > 3 ) {
      return shell.evaluate(script.substring(2, script.length() - 1));
    } else {
      return "<invalid closure>";
    }
  }

  private Object getValue(Binding binding, String variableName, BehaviorStep parentStep) {
    if ("#stepName".equalsIgnoreCase(variableName) && parentStep != null  ) {
      return parentStep.getName();
    } else {
      try {
      return binding.getProperty(variableName.substring(1));
      } catch ( MissingPropertyException mpe ) {
        return "#{" + variableName + "}";
      }
    }
  }

//
//  public static void main(String[] args) {
//    Binding b = new Binding();
//    b.setProperty( "ikea", new Integer(3));
//    System.out.println(new TextDecoder("#{ikea + 7} is #ikea idea").replace(b, null));
//  }
}
