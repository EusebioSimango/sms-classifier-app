# Google Play — submissão e permissões de SMS

A política [Use of SMS or Call Log permission groups](https://support.google.com/googleplay/android-developer/answer/10208820) só permite `READ_SMS` e `RECEIVE_SMS` a aplicações que sejam a app de SMS predefinida, ou a aplicações que se enquadrem numa excepção temporária. Uma aplicação que apenas organiza SMS cai em «SMS and contacts management (when the app is not the default handler)», uma das utilizações **não permitidas**.

## Caminhos possíveis

**A. Excepção «Caller ID, spam detection, and/or spam blocking».** Cobre `READ_SMS` e `RECEIVE_SMS`. A função central da aplicação tem de ser a detecção de SMS fraudulentos ou de spam, e a descrição na loja tem de o destacar. A excepção «Anti-smishing» exige historial comprovado e não se aplica. A aprovação não é garantida.

**B. Ser a app de SMS predefinida.** É o caminho mais seguro para aprovação, mas exige `RoleManager.ROLE_SMS`, envio de SMS, recepção de MMS, `SMS_DELIVER`, um serviço `RESPOND_VIA_MESSAGE` e um ecrã de composição. Está fora do âmbito e do calendário do TCC.

**C. Distribuição fora da loja.** Para o TCC, o APK é instalado por ADB. As exigências abaixo ficam documentadas como preparação para uma submissão futura.

## O que o Play exige em qualquer caminho

1. **Aviso destacado e consentimento** antes do pedido de permissão do sistema. Já implementado no ecrã de entrada.
2. **Política de privacidade** num URL público. O texto está em [politica-privacidade.md](politica-privacidade.md).
3. **Formulário de Segurança dos dados** (Data safety):
   - Dados recolhidos: nenhum.
   - Dados partilhados: nenhum.
   - Dados cifrados em trânsito: não se aplica, porque não há transmissão.
   - O utilizador pode pedir a eliminação dos dados: sim, ao desinstalar a aplicação.
4. **Declaração de permissões** (Permissions Declaration Form), com a função central, um vídeo de demonstração e a justificação de não existir alternativa.

## Lista de verificação técnica

- [x] Sem permissão `INTERNET` (removida, porque o ONNX Runtime a declarava para telemetria).
- [x] Telemetria do ONNX Runtime desactivada (`setTelemetry(false)`) e o `TelemetryInitializer` removido do manifesto.
- [x] `allowBackup="false"` e regras de exclusão para cópia de segurança e transferência entre dispositivos.
- [x] `usesCleartextTraffic="false"`.
- [x] Base cifrada com SQLCipher; chave aleatória em `EncryptedSharedPreferences`.
- [x] `SmsReceiver` protegido com `BROADCAST_SMS`.
- [x] `FLAG_SECURE` na actividade principal.
- [x] R8 e redução de recursos activos na versão release; regras de ofuscação para o ONNX Runtime e o SQLCipher.
- [ ] Assinatura release com chave própria, guardada fora do repositório.
- [ ] Publicar a política de privacidade num URL.
- [ ] Conta de programador Google Play e preenchimento dos formulários.
