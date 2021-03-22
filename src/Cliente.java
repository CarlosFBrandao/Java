import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente extends Thread {
	
	// Propriedades
	private BufferedReader entrada;
	
	// a variável de conexão é estática porque o seu conteúdo é compartilhado entre os vários objetos da classe Cliente de um mesmo console
	private static String mensagem;
	
	// Método Construtor
	public Cliente (BufferedReader i) {
		// define a propriedade que armazena o objeto que recebe 
		entrada = i;
		// define a propriedade para não permitir que o loop seja finalizado
		mensagem = "Not Null";
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		// Declaração de variáveis de controle
		String mensagem_recebida;
		String nome_cliente;
		String opcao;
		
		// variável que armazena o objeto que gerencia os dados recebidos do teclado
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
		
		// Mostra a mensagem no console
		System.out.println("Informe o nome do cliente:");
		
		// Recebe o Nome do cliente
		nome_cliente = teclado.readLine();
		
		// Faz a conexão com o servidor Local na porta 8657
		Socket conexao = new Socket("localhost", 8657);
		
		// Instanciando método que recebe as mensagens que serão enviadas para o servidor
		DataOutputStream saida_servidor = new DataOutputStream(conexao.getOutputStream());
		
		// Instanciando método que recebe mensagens recebidas do servidor
		BufferedReader entrada_server = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
		
		// Envia o nome do cliente para o servidor
		saida_servidor.writeBytes(nome_cliente + '\n');
		
		// Faz a leitura da mensagem do servidor
		mensagem_recebida = entrada_server.readLine();
		
		// Imprime no console a mensagem
		System.out.println(mensagem_recebida);
		
		// Mensagem para seleção do assunto desejado pelo cliente
		while(true) {
			System.out.println("Informe um assunto: 1- Economia | 2- Entretenimento | 3- Tecnologia");
		
			// Variável que armazena opção selecionada pelo cliente
			opcao = teclado.readLine();
			
			if (!opcao.equals("1") && !opcao.equals("2") && !opcao.equals("3")) {
				System.out.println("Opção Inválida");
			}
			else
				break;			
			
		}		
		
		// Envia a opção selecionada para o server
		saida_servidor.writeBytes(opcao + '\n');
		
		// recebe o retorno do servidor
		mensagem_recebida = entrada_server.readLine();
		
		// Imprime no console a mensagem recebida
		System.out.println(mensagem_recebida);
		
		// Instancia uma Thread para receber as mensagens do servidor
		Thread t = new Cliente (entrada_server);
		
		// Inicializa a Thread
		t.start();
		
		// Loop infinito para receber e enviar mensagens para o servidor
		while (true) {
			
			mensagem = teclado.readLine();
			saida_servidor.writeBytes(mensagem + '\n');
			
			//Se a mensagem for igual a "fim" o sistema encerra o loop
			if (mensagem.startsWith("fim") == true)
			break;
			
		}
		
		mensagem_recebida = entrada_server.readLine();
		
		// Tratamento para só imprimir no console caso a mensagem recebida anteriormente for diferente de null
		if (mensagem_recebida != null) {
			System.out.println(mensagem_recebida);
		}
		
		// Encerra a conexão do cliente e encerra a aplicação de forma normal
		conexao.close();
		
	}
	
	public void run() {
		try {
			// Verifica se o chat deve ser encerrado
			while (mensagem != null && !(mensagem.trim().equals("")) && !(mensagem.startsWith("fim"))) {
				System.out.println(entrada.readLine());
			}
			//Tratamento de exceções
			System.exit(0);
		} catch (IOException e) {
			System.exit(0);
		}
	}
	
}