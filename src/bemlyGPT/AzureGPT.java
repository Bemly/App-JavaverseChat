package bemlyGPT;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.ai.openai.models.Choice;
import com.azure.ai.openai.models.Completions;
import com.azure.ai.openai.models.CompletionsOptions;
import com.azure.ai.openai.models.CompletionsUsage;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpClient;
import com.azure.core.util.Header;
import com.azure.core.util.HttpClientOptions;
import com.azure.core.util.IterableStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

public class AzureGPT {

    public static void main(String[] args) {
        String azureOpenaiKey = "";	// ��Կ
        String endpoint = "";	// �ս��
        String modelId = "";	// GPT3.5ģ��id

        OpenAIClient client = new OpenAIClientBuilder()
        	.httpClient(HttpClient.createDefault(new HttpClientOptions()
        	.setHeaders(Collections.singletonList(new Header("Accept-Charset", "utf-8")))))
            .endpoint(endpoint)
            .credential(new AzureKeyCredential(azureOpenaiKey))
            .buildClient();
        

        List<ChatMessage> chatMsgList = new ArrayList<>();
        chatMsgList.add(new ChatMessage(ChatRole.SYSTEM).setContent("�����������"));
        try {
        	IterableStream<ChatCompletions> completionsStream = client.getChatCompletionsStream(modelId, new ChatCompletionsOptions(chatMsgList));
        	completionsStream.forEach(completion -> {
    		    for (ChatChoice choice : completion.getChoices()) {
    		    	try { Thread.sleep(100); } catch (Exception e) { e.printStackTrace(); }
    		        ChatMessage message = choice.getDelta();
    		        if (message != null && message.getContent() != null) {
    		            System.out.print(message.getContent());
    		        }
    		    }
    		});
        } catch (HttpResponseException e) {
			JOptionPane.showMessageDialog(null, e, "���棺Υ��OpenAI GPT���Թ涨��", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
        }
    }
}


/*
 * ���Ӱ�����	��Ҫ��ʱ����ȡ	�Ǵ�GithubIssue�Ĵ����ύ����open״̬����ר���Ĵ���
 */
//ChatCompletions chatCompletions = client.getChatCompletions(modelId, new ChatCompletionsOptions(chatMessages));

//System.out.printf("ģ��id:%s ������ %d.%n", chatCompletions.getId(), chatCompletions.getCreated());
//for (ChatChoice choice : chatCompletions.getChoices()) {
//  ChatMessage message = choice.getMessage();
//  System.out.printf("����: %d, ����Ȩ��: %s.%n", choice.getIndex(), message.getRole());
//  System.out.println("��Ϣ:");
//  System.out.println(message.getContent());
//}
//
//System.out.println();
//CompletionsUsage usage = chatCompletions.getUsage();
//System.out.printf("ʹ����: (prompt token)��ʾ������������Ϊ  %d, "
//      + "�����������Ϊ %d, �������Ӧ�е�����������Ϊ %d.%n",
//  usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
//  
//  
//  System.out.println();
//  System.out.println();
//  System.out.println();
//  System.out.println();
//  
//  chatMessages.add(new ChatMessage(ChatRole.ASSISTANT).setContent("���Ͼ仰˵��ʲô"));
//  chatCompletions = client.getChatCompletions(modelId, new ChatCompletionsOptions(chatMessages));
//  System.out.printf("ģ��id:%s ������ %d.%n", chatCompletions.getId(), chatCompletions.getCreated());
//  for (ChatChoice choice : chatCompletions.getChoices()) {
//      ChatMessage message = choice.getMessage();
//      System.out.printf("����: %d, ����Ȩ��: %s.%n", choice.getIndex(), message.getRole());
//      System.out.println("��Ϣ:");
//      System.out.println(message.getContent());
//  }
//
//  System.out.println();
//  usage = chatCompletions.getUsage();
//  System.out.printf("ʹ����: (prompt token)��ʾ������������Ϊ  %d, "
//          + "�����������Ϊ %d, �������Ӧ�е�����������Ϊ %d.%n",
//      usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
  
//  List<ChatMessage> chatMessages = new ArrayList<>();
//  chatMessages.add(new ChatMessage(ChatRole.ASSISTANT).setContent("����дһ��JDBC�Ĵ��룬��Ҫʵ�����ݿ�mydb��user��name��pwd����ɾ�Ĳ�"));
//  ChatCompletions chatCompletions = client.getChatCompletions(modelId, new ChatCompletionsOptions(chatMessages));
//  System.out.printf("ģ��id:%s ������ %d.%n", chatCompletions.getId(), chatCompletions.getCreated());
//  for (ChatChoice choice : chatCompletions.getChoices()) {
//      ChatMessage message = choice.getMessage();
//      System.out.printf("����: %d, ����Ȩ��: %s.%n", choice.getIndex(), message.getRole());
//      System.out.println("��Ϣ:");
//      System.out.println(message.getContent());
//  }

//  List<String> prompt = new ArrayList<>();
//  prompt.add("");