# Guia de laboratório

Instruções para reproduzir o protótipo num computador com Android SDK e num telemóvel de teste (por exemplo Samsung Galaxy A06). Destina-se a sessões de demonstração da monografia e a desenvolvimento local.

## Requisitos

- JDK 17 (ou o JBR do Android Studio)
- Android SDK com plataforma API 36
- `ANDROID_HOME` (por omissão nos *scripts*: `/home/esimango/Android/Sdk`)
- Telemóvel com depuração USB activa, ou emulador

## Compilar e instalar

Na raiz do projecto Android (`android-app`):

```bash
./build.sh && ./install-debug.sh
```

- `build.sh` executa `./gradlew assembleDebug` e indica o caminho do APK.
- `install-debug.sh` instala por ADB (`adb install -r`). Se o APK ainda não existir, compila-o.

APK gerado:

`app/build/outputs/apk/debug/app-debug.apk`

É um *build* de depuração, assinado com a chave de desenvolvimento. Não é a versão final da monografia (Fase 8).

## Se o ADB recusar o dispositivo

Mensagens `unauthorized` significam que o telemóvel ainda não autorizou este computador. Aceitar o diálogo de depuração USB no ecrã e voltar a correr `./install-debug.sh`.

## Se a instalação pelo ficheiro for bloqueada

O Play Protect recusa APKs com permissões de SMS vindos de fontes da Internet (WhatsApp, Drive, gestor de ficheiros). Para o TCC, instalar **sempre por USB** com o *script* acima.

Se a app instalar mas o interruptor de SMS estiver cinzento: Definições → Aplicações → Categorizador SMS → menu → **Permitir definições restritas**. Depois abrir a app e conceder `RECEIVE_SMS` e `READ_SMS`.

## Duas aplicações no telemóvel

O identificador passou de `mz.pycommunity.smsclassifier` para `mz.feng.smsclassifier`. O Android trata-as como aplicações distintas. Desinstalar a cópia antiga (PyCommunity) para evitar confusão na demonstração.

## Roteiro de teste (Fase 1)

1. Abrir **Categorizador SMS** e conceder permissões, se pedidas.
2. Verificar que a lista mostra conversas (remetente, pré-visualização, contagem).
3. Abrir uma conversa (por exemplo um operador) e confirmar as mensagens desse remetente; voltar com **Voltar** ou o botão do sistema.
4. Enviar um SMS de teste para o número do dispositivo e confirmar que a conversa se actualiza (a app deve estar instalada e com permissão).
5. No Logcat, filtrar `SmsReceiver`: não devem aparecer o corpo nem o número da mensagem.

## Emulador

*Extended controls* → *Phone* → *Send SMS*. As permissões e o receptor comportam-se de forma análoga, sem o bloqueio do Play Protect associado ao *sideload* por ficheiro.
