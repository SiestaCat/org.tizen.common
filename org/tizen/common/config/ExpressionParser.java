package org.tizen.common.config;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

public class ExpressionParser {
  protected static final char CH_VAR = '$';
  
  protected static final char CH_VAR_OPEN = '{';
  
  protected static final char CH_VAR_CLOSE = '}';
  
  protected static final char CH_ESCAPE = '^';
  
  protected enum Status {
    ST_EXP, ST_ESCAPE, ST_VAR;
  }
  
  protected static String parse(String exp) {
    Status status = Status.ST_EXP;
    if (exp == null)
      return null; 
    StringReader reader = new StringReader(exp);
    StringWriter writer = new StringWriter();
    int ch = 0;
    StringWriter variableWriter = new StringWriter();
    try {
      HashMap<String, String> var2val = new HashMap<>();
      while ((ch = reader.read()) >= 0) {
        switch (status) {
          case ST_EXP:
            if (36 == ch) {
              ch = reader.read();
              if (123 == ch) {
                status = Status.ST_VAR;
                variableWriter = new StringWriter();
                continue;
              } 
              writer.write(36);
            } else if (94 == ch) {
              status = Status.ST_ESCAPE;
            } 
          case null:
            writer.write((char)ch);
            status = Status.ST_EXP;
          case ST_VAR:
            if (125 == ch) {
              String variable = variableWriter.toString();
              String cached = var2val.get(variable);
              String subVal = 
                (cached == null) ? Preference.getValue(variable, null) : cached;
              if (subVal == null) {
                writer.write(36);
                writer.write(123);
                writer.write(variable);
                writer.write(125);
              } else {
                var2val.put(variable, subVal);
                writer.write(subVal);
              } 
              status = Status.ST_EXP;
              continue;
            } 
            variableWriter.write((char)ch);
        } 
      } 
      if (Status.ST_VAR == status) {
        writer.write(36);
        writer.write(123);
        writer.write(variableWriter.toString());
      } 
    } catch (IOException e) {
      throw new IllegalStateException("StringReader#read throw IOException", e);
    } 
    return writer.toString();
  }
}
