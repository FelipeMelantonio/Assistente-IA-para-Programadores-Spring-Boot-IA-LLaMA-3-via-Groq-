import { useState, useRef, useEffect } from 'react';

export default function Chat() {
  const [messages, setMessages] = useState([]); // Estado das mensagens
  const [input, setInput] = useState(''); // Estado do input do usuário
  const [language, setLanguage] = useState('java'); // Estado da linguagem selecionada
  const chatEndRef = useRef(null); // Referência para o fim do chat, para rolar para a última mensagem

  // Função para enviar a mensagem
  const sendMessage = async () => {
    const trimmedInput = input.trim();
    if (!trimmedInput) return; // Não envia se o input estiver vazio

    const userMessage = { role: 'user', content: trimmedInput }; // Mensagem do usuário
    setMessages((prev) => [...prev, userMessage]); // Adiciona a mensagem do usuário no estado
    setInput(''); // Limpa o campo de input

    try {
      // Envia a requisição para o backend
      const response = await fetch('http://localhost:8080/projects/1/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          prompt: trimmedInput, // O prompt para a IA
          language: language, // A linguagem selecionada
        }),
      });

      if (!response.ok) {
        throw new Error('Falha ao se comunicar com o servidor');
      }

      const data = await response.json(); // Recebe a resposta da IA como JSON
      const iaMessage = { role: 'ia', content: data.generatedCode }; // Agora usamos o campo generatedCode
      setMessages((prev) => [...prev, iaMessage]); // Adiciona a mensagem da IA no estado

    } catch (error) {
      console.error(error);
      const errorMessage = { role: 'ia', content: 'Erro ao gerar código. Tente novamente mais tarde.' };
      setMessages((prev) => [...prev, errorMessage]);
    }
  };

  // Função para detectar a tecla Enter
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) { // Enviar mensagem quando pressionado Enter sem Shift
      e.preventDefault();
      sendMessage();
    }
  };

  // Efeito para rolar até a última mensagem
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]); // Executa sempre que as mensagens mudam

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      <header style={{ padding: '16px', backgroundColor: '#202123', color: 'white' }}>
        <h2>Chat com IA - Geração de Código</h2>
      </header>

      <main style={{ flex: 1, padding: '16px', overflowY: 'auto', background: '#f1f1f1' }}>
        {messages.map((msg, idx) => (
          <div
            key={idx}
            style={{
              marginBottom: '12px',
              textAlign: msg.role === 'user' ? 'right' : 'left',
            }}
          >
            <div
              style={{
                display: 'inline-block',
                padding: '12px',
                borderRadius: '12px',
                backgroundColor: msg.role === 'user' ? '#4f46e5' : '#e2e8f0',
                color: msg.role === 'user' ? 'white' : 'black',
                maxWidth: '80%',
                whiteSpace: 'pre-wrap',
              }}
            >
              {msg.content}
            </div>
          </div>
        ))}
        <div ref={chatEndRef} /> {/* Ref para rolar até o final */}
      </main>

      <footer style={{ padding: '16px', backgroundColor: '#ffffff', borderTop: '1px solid #ddd' }}>
        <div style={{ marginBottom: 8 }}>
          <select value={language} onChange={(e) => setLanguage(e.target.value)}>
            <option value="java">Java</option>
            <option value="python">Python</option>
            <option value="javascript">JavaScript</option>
          </select>
        </div>
        <textarea
          rows="3"
          style={{ width: '100%', padding: '8px', borderRadius: '8px', border: '1px solid #ccc' }}
          placeholder="Digite sua mensagem aqui... (Enter para enviar)"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyPress}
        />
      </footer>
    </div>
  );
}
