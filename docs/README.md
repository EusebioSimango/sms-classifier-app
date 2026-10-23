# Documentação de apoio à monografia

Este directório descreve o protótipo Android **Categorizador SMS**, desenvolvido no âmbito de um trabalho de conclusão de curso (TCC) na Faculdade de Engenharia (FENG). O processamento é estritamente local e offline.

Os textos não substituem os capítulos da monografia. Servem como anexo técnico, memória de desenvolvimento e guião de reprodução em laboratório.

## Identificação do software

| Campo | Valor |
| --- | --- |
| Nome visível | Categorizador SMS |
| `applicationId` / *namespace* | `mz.feng.smsclassifier` |
| Instituição no identificador | FENG (Faculdade de Engenharia) |
| Linguagem | Kotlin |
| Interface | Jetpack Compose |

O prefixo `mz.feng` segue a convenção de nomes invertidos (*reverse DNS*) usada em Android: país (`mz`) e unidade académica (`feng`). O produto (`smsclassifier`) distingue a aplicação no dispositivo.

## Conteúdo

1. [Plano de desenvolvimento e estado](plano-desenvolvimento.md) — fases previstas, requisitos e o que já foi implementado.
2. [Protótipo (Fases 1–2)](prototipo-fase-1.md) — arquitectura, persistência cifrada, decisões e limitações.
3. [Guia de laboratório](guia-laboratorio.md) — compilação, instalação e testes no dispositivo.
4. [Política de privacidade](politica-privacidade.md) — texto pronto a publicar.
5. [Google Play](google-play.md) — permissões de SMS, formulários e lista de verificação.

## Como citar na monografia

No capítulo de desenvolvimento ou de metodologia, pode remeter-se para estes ficheiros como documentação do protótipo (por exemplo: «o estado das fases e o mapeamento de requisitos encontram-se no anexo técnico»). Os resultados do modelo de classificação (Fase 4) ainda não existem e **não devem ser inventados** neste material.
