package api;

import java.io.FileNotFoundException;

/**
 * API for the mapping between architectural concepts used in architecture rules and the code model.
 * Generates an OWL ontology file in which the code model, the architecture model, and the mapping between them
 * are contained. This mapping is performed according to some architecture-to-code mapping rules, which
 * must be specified in an SWRL file.
 */
public interface ExecuteMappingAPI {
	/**
	 * Sets the configuration and the path of the file in which the output file will be stored.
	 * @param config the configuration to use
	 * @param outputFilePath the path of the file in which the output ontology is stored
	 */
	public void setReasoningConfiguration(ReasoningConfiguration config, String outputFilePath);
	
	/**
	 * Reads the input ontologies and attempts to unify them. The result is written to the
	 * output ontology.
	 * @throws FileNotFoundException when an input or output file cannot be accessed
	 */
	public void executeMapping() throws FileNotFoundException;
	
	/**
	 * Returns the path of the output file which was given to {@link #setReasoningConfiguration(ReasoningConfiguration, String)} 
	 */
	public String getReasoningResultPath();
	
}
