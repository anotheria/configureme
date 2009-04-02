package org.configureme;

import java.io.IOException;
import java.util.List;

import org.configureme.parser.ConfigurationParser;
import org.configureme.parser.ConfigurationParserException;
import org.configureme.parser.ParsedArtefact;
import org.configureme.parser.ParsedAttribute;
import org.configureme.parser.json.JsonParser;
import org.configureme.repository.Artefact;
import org.configureme.repository.ConfigurationRepository;

import net.anotheria.util.IOUtils;
import net.anotheria.util.StringUtils;

public enum ConfigurationManager {
	
	INSTANCE;
	
	public Configuration getConfiguration(String artefactName){
		return getConfiguration(artefactName, GlobalEnvironment.INSTANCE);
	}
	
	public Configuration getConfiguration(String artefactName, Environment in){
		Configuration config = null;
		try{
			config = ConfigurationRepository.INSTANCE.getConfiguration(artefactName, in);
		}catch(Exception e){
			//only for prototype, 'real' versions will have proper exception handling
		}
		
		if (config==null){
			readConfigFromFile(artefactName);
		}

		
		config = ConfigurationRepository.INSTANCE.getConfiguration(artefactName, in);
		return config;
	}
	
	private void readConfigFromFile(String artefactName){
		String fileName = "src/examples/"+artefactName+".json";
		try{
			
			String content = IOUtils.readFileAtOnceAsString(fileName);
			content = StringUtils.removeCComments(content);
			content = StringUtils.removeCPPComments(content);
			ConfigurationParser parser = new JsonParser();
			ParsedArtefact pa = parser.parseArtefact(artefactName, content);
			System.out.println("Parsed "+pa);
			List<ParsedAttribute> attributes = pa.getAttributes();
			Artefact art = ConfigurationRepository.INSTANCE.createArtefact(artefactName);
			for (ParsedAttribute a : attributes){
				art.addAttributeValue(a.getName(), a.getValue(), a.getEnvironment());
			}
			
		}catch(IOException e){
			throw new IllegalArgumentException("File "+fileName+" not found (this exception should be replaced later)");
		}catch(ConfigurationParserException e){
			throw new IllegalArgumentException(fileName+" unparseable (this exception should be replaced later)");
		}
	}
	
}
