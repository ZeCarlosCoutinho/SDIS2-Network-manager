import java.io.IOException;

public class Client {
	public static void main(String args[]) {
		if(args.length != 0) {
			System.out.println("Client takes no arguments");
			return;
		}
		String username = CredentialAsker.requestUsername();
		String password = CredentialAsker.requestPassword();
		try {
			Registrator registrator = new Registrator("https://sigarra.up.pt/feup/pt/mob_val_geral.autentica?");
			if(registrator.register(username, password)) {
				//TODO Database operations
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
}
