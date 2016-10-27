package com.zygen.hcp.model.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zygen.hcp.jpa.Command;

/**
 * Stax Parser Implementation
 * 
 */
public class XMLParser {
	static final String COMMANDDATA = "CommandData";
	static final String COMMANDS = "Commands";
	static final String COMMAND = "command";
	static final String SOURCESYSTEM = "sourceSystem";
	static final String LANGU = "langu";
	static final String DESCRIPTION = "description";
	static final String PATTERN = "pattern";

	static Logger logger = LoggerFactory.getLogger(XMLParser.class);

	private InputStream in = null;

	public static void main(String[] args) throws Exception {
		EntityManager em = null;
		List<Command> commands = new XMLParser().readCommand(em, "com/zygen/hcp/model/data/Commands.xml");
		System.out.println(commands.toString());

	}

	public List<Command> readCommand(EntityManager em, String commandXml) {
		List<Command> commands = new ArrayList<Command>();

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader = null;
		try {

			in = getResourceAsInputStream(commandXml);
			reader = inputFactory.createXMLStreamReader(in);
			inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
			// Command command = null;
			while (reader.hasNext()) {
				reader.next();
				// System.out.println(reader.getLocalName());
				// System.out.println(reader.getEventType());
				switch (reader.getEventType()) {
				case XMLStreamReader.START_ELEMENT:
					if (reader.getLocalName() == (COMMANDDATA)) {
						System.out.println(reader.getLocalName());
						commands = readCommands(reader);
					}
					break;

				case XMLStreamReader.END_ELEMENT:
					// commands.add(command);
					break;
				}

			}

		} catch (Exception e) {
			logger.error("Exception occured", e);
			System.out.println(e.getMessage());
		} finally {
			try {
				in.close();
				reader.close();
			} catch (IOException e) {
				logger.error("IO Exception occured", e);

			} catch (XMLStreamException e) {
				logger.error("XMLStream exception occured", e);

			}
		}
		return commands;

	}

	private List<Command> readCommands(XMLStreamReader reader) throws XMLStreamException {
		List<Command> commands = Arrays.asList(new Command());
		while (reader.hasNext()) {
			int eventType = reader.next();
			Command command = new Command();
			switch (eventType) {
			case XMLStreamReader.START_ELEMENT:
				System.out.println(reader.getLocalName());
				if (reader.getLocalName() == (COMMANDS)) {

					command = readCommand(reader);
				}
				break;
			case XMLStreamReader.CHARACTERS:
				break;
			case XMLStreamReader.END_ELEMENT:
				commands.add(command);
				break;
			}
		}
		return commands;
	}

	private Command readCommand(XMLStreamReader reader) throws XMLStreamException {
		Command command = new Command();
		while (reader.hasNext()) {
			int eventType = reader.next();

			switch (eventType) {
			case XMLStreamReader.START_ELEMENT:
				System.out.println(reader.getLocalName());
				if (reader.getLocalName() == (COMMAND)) {
					command.setCommand(readCharacters(reader));
				} else if (reader.getLocalName() == (SOURCESYSTEM)) {
					command.setCommand(readCharacters(reader));
				} else if (reader.getLocalName() == (LANGU)) {
					command.setCommand(readCharacters(reader));
				} else if (reader.getLocalName() == (DESCRIPTION)) {
					command.setCommand(readCharacters(reader));
				}else if (reader.getLocalName() == (PATTERN)) {
					command.setCommand(readCharacters(reader));
				}
				break;
			case XMLStreamReader.CHARACTERS:

				break;

			case XMLStreamReader.END_ELEMENT:

				break;
			}
			
		}
		return command;
	}

	public InputStream getResourceAsInputStream(String xmlFile) {
		return XMLParser.class.getClassLoader().getResourceAsStream(xmlFile);
	}

	private String readCharacters(XMLStreamReader reader) throws XMLStreamException {
		StringBuilder result = new StringBuilder();
		while (reader.hasNext()) {
			int eventType = reader.next();
			switch (eventType) {
			case XMLStreamReader.CHARACTERS:
			case XMLStreamReader.CDATA:
				result.append(reader.getText());
				System.out.println(reader.getText());
				break;
			case XMLStreamReader.END_ELEMENT:
				return result.toString();
			}
		}
		throw new XMLStreamException("Premature end of file");
	}

}
