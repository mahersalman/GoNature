
package client;

import java.io.IOException;

public class ClientController implements ChatIF {
	// Class variables *************************************************

	/**
	 * The default port to connect on.
	 */
	public static int DEFAULT_PORT;

	// Instance variables **********************************************

	/**
	 * The instance of the client that created this ConsoleChat.
	 */
	ChatClient client;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 * @throws IOException
	 */
	public ClientController(String host, int port) throws IOException {
		try {
			client = new ChatClient(host, port, this);
			client.join();
		} catch (IOException e) {
			throw new IOException("ConnectionFailed");
		}

	}

	// Instance methods ************************************************

	/**
	 * This method waits for input from the console. Once it is received, it sends
	 * it to the client's message handler.
	 */
	public void accept(Object msg) {
		client.handleMessageFromClientUI(msg);
	}


	@Override
	public void display(String message) {
	}
}
//End of ConsoleChat class
