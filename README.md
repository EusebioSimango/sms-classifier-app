# Categorizador SMS

Protótipo Android de categorização automática de SMS com processamento local e offline. Trabalho de conclusão de curso (TCC).

Identificador da aplicação: `mz.smsclassifier`.

A lista agrupa por conversa. Dá para responder a números de telefone, começar uma mensagem com **+** e, se os contactos forem autorizados, ver o nome da pessoa em vez do número. Com dois cartões, o envio pode sair pelo SIM escolhido. Com notificações autorizadas, um aviso aparece quando chega um SMS: desbloqueado mostra o texto; no ecrã de bloqueio só diz que chegou uma mensagem. Códigos de operadora e de banco não têm resposta. O detalhe está em [docs/prototipo-fase-1.md](docs/prototipo-fase-1.md).

## Compilar e instalar

```bash
./build.sh && ./install-debug.sh
```

Para substituir o modelo, o ficheiro tem de ser `.onnx`, chamar-se `modelo_sms_mocambique.onnx` e ficar em `app/src/main/assets/models/`. O passo a passo está em [docs/guia-laboratorio.md](docs/guia-laboratorio.md).

## Documentação

Índice e contexto da monografia: [docs/README.md](docs/README.md)

| Documento | Caminho | Conteúdo |
| --- | --- | --- |
| Plano e estado | [docs/plano-desenvolvimento.md](docs/plano-desenvolvimento.md) | Fases 0–8, requisitos e o que já está implementado |
| Protótipo (Fases 1–2) | [docs/prototipo-fase-1.md](docs/prototipo-fase-1.md) | Arquitectura, persistência cifrada, privacidade e limitações |
| Guia de laboratório | [docs/guia-laboratorio.md](docs/guia-laboratorio.md) | Compilação, instalação por USB, substituição do modelo e roteiro de teste |
