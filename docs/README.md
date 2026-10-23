# Documentação de apoio à monografia

Este directório descreve o protótipo Android **Categorizador SMS**, desenvolvido no âmbito de um trabalho de conclusão de curso (TCC). O processamento é estritamente local e offline.

Os textos não substituem os capítulos da monografia. Servem como anexo técnico, memória de desenvolvimento e guião de reprodução em laboratório.

## Identificação do software

| Campo | Valor |
| --- | --- |
| Nome visível | Categorizador SMS |
| `applicationId` / *namespace* | `mz.smsclassifier` |
| Linguagem | Kotlin |
| Interface | Jetpack Compose |

O identificador `mz.smsclassifier` segue a convenção de nomes invertidos (*reverse DNS*): país (`mz`) e produto (`smsclassifier`).

## Conteúdo

1. [Plano de desenvolvimento e estado](plano-desenvolvimento.md) — fases previstas, requisitos e o que já foi implementado.
2. [Protótipo (Fases 1–2)](prototipo-fase-1.md) — arquitectura, persistência cifrada, decisões e limitações.
3. [Guia de laboratório](guia-laboratorio.md) — compilação, instalação e testes no dispositivo.
4. [Política de privacidade](politica-privacidade.md) — texto pronto a publicar.
5. [Google Play](google-play.md) — permissões de SMS, formulários e lista de verificação.

## Como citar na monografia

No capítulo de desenvolvimento ou de metodologia, pode remeter-se para estes ficheiros como documentação do protótipo (por exemplo: «o estado das fases e o mapeamento de requisitos encontram-se no anexo técnico»). As métricas do modelo (precisão, matriz de confusão) pertencem à monografia e não são reproduzidas aqui.
