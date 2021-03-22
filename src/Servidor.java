import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Servidor extends Thread {
	
	// Proriedades
	// Declara��o e instacia��o do socket e vetores que armazer�o os clientes de acordo com o assunto selecionado
	private Socket conexao;
	private static Vector<DataOutputStream> vetorEconomia = new Vector<DataOutputStream>();
	private static Vector<DataOutputStream> vetorEntretenimento = new Vector<DataOutputStream>();
	private static Vector<DataOutputStream> vetorTecnologia = new Vector<DataOutputStream>();
	
	// M�todo construtor
	public Servidor (Socket s) {
		conexao =  s;
	}

	
	public static void main(String[] args) throws IOException {

		// Cria o socket para conex�es na porta 8657
		@SuppressWarnings("resource")
		ServerSocket servidor = new ServerSocket(8657);

		// Loop infinito que aguarda a conex�o dos clientes
		while(true) {
			
			System.out.println("Esperando cliente se conectar...");
			
			// Se um cliente iniciar uma conex�o, a classe Server aceita esta conex�o.
			Socket cn = servidor.accept();
			
			// declara a Thread chamando o m�todo contrutor e passa como parametro a conex�o recebida
			Thread t = new Servidor (cn);
			
			// Inicia a Thread
			t.start();
			
			System.out.println("Cliente conectado!");
		}

	}
	
	// implementa o m�todo run () da classe Thread. Este m�todo � executado ap�s a chamada do m�todo start ()
	public void run() {
		
		// Declara��o das vari�veis de controle
		String mensgem_recebida;
		String mensgem_enviada;
		String nome_cliente;
		String assunto_selecionado;
		
		// Vari�vel para entrada de mensagens
		BufferedReader entrada_cliente;
		
		try {
			
			//Vari�vel para receber as mensagens do cliente			
			entrada_cliente = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			
			// Vari�vel para envio de mensagens para o cliente
			DataOutputStream saida_cliente = new DataOutputStream(conexao.getOutputStream());

			// recebe o nome do cliente
			nome_cliente = entrada_cliente.readLine();
			
			// Envio do retorno ao cliente
			saida_cliente.writeBytes("<Servidor> : <Ol� " + nome_cliente + "!> : <" + getDateTime() + ">\n");
			
			//Armazena a op��o selcionada pelo cliente
			assunto_selecionado = entrada_cliente.readLine();
			
			
			Integer i;
			Vector<DataOutputStream> v;
			
			// adiciona os clientes aos vetores de acordo com o assunto escolhido
			switch(assunto_selecionado) {
			  case "1":
				  vetorEconomia.add(saida_cliente);
				  v = vetorEconomia;
				  assunto_selecionado = "Economia";
			    break;
			  case "2":
				  vetorEntretenimento.add(saida_cliente);
				  v = vetorEntretenimento;
				  assunto_selecionado = "Entretenimento";
			    break;
			  case "3":
				  vetorTecnologia.add(saida_cliente);
				  v = vetorTecnologia;
				  assunto_selecionado = "Tecnologia";
			    break;
			  default:
				  vetorEntretenimento.add(saida_cliente);
				  v = vetorEntretenimento;
				  assunto_selecionado = "Entretenimento";
			}
			
			// Envia as mensagens para todos cliente de acordo com o assunto
			i = 0;
			while (i < v.size()) {
				v.get(i).writeBytes("<Servidor> : <" + nome_cliente + " entrou no chat [" + assunto_selecionado + "]!> : <" + getDateTime() + ">\n");
				i = i + 1;
			}
			
			// Recebe as mensagens do cliente
			mensgem_recebida = entrada_cliente.readLine();

			// Enquanto as mensagens forem diferentes de null ou "fim" ser� executado o loop
			while (mensgem_recebida != null && !(mensgem_recebida.trim().equals("")) && !(mensgem_recebida.startsWith("fim"))) {
				
				System.out.println(nome_cliente + ": " + mensgem_recebida);

				// Formata as mensagens que ser�o enviadas para os clientes
				mensgem_enviada = " <" + nome_cliente + "> : <" + assunto_selecionado + "> : <" + mensgem_recebida + "> : <" + getDateTime() + ">\n";

				i = 0;
				while (i < v.size()) {
					if(v.get(i) != saida_cliente) {				
						// Envia retorno para os clientes
						v.get(i).writeBytes(mensgem_enviada);
					}
					i = i + 1;
				}
				
				// Aguarda novas mensagens dos clientes
				mensgem_recebida = entrada_cliente.readLine();
				
			}

			// Loop para envio das mensagens formatadas para os clientes quando algu�m se desconecta do chat
			i = 0;
			while (i < v.size()) {
				v.get(i).writeBytes("<Servidor> : <" + nome_cliente + " saiu do chat [" + assunto_selecionado + "]!> : <" + getDateTime() + ">\n");
				i = i + 1;
			}
			
			// Remove o cliente desconectado do vetor
			i = 0;
			while (i < v.size()) {
				if(v.get(i) == saida_cliente) {
					v.remove(v.get(i));
					System.out.println("Cliente desconectado!");
				}
				i = i + 1;
			}
			
			// encerra a conex�o do cliente
			conexao.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//m�todo para buscar a hora e data das mensagens
	private static String getDateTime() { 
		DateFormat dateFormat = new SimpleDateFormat("HH:mm"); 
		Date date = new Date(); 
		return dateFormat.format(date); 
	}

}