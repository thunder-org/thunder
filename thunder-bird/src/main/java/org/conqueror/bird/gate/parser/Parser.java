package org.conqueror.bird.gate.parser;

import org.conqueror.bird.data.BirdData;
import org.conqueror.bird.exceptions.parse.ParserException;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.Map;


public interface Parser extends BirdData {

    Collection<Map<String, Object>> parse(BufferedReader reader) throws ParserException;

}
