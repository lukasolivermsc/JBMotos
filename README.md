# JB Motos

Aplicativo Android desenvolvido em Java utilizando o Android Studio, criado como projeto de faculdade.

## 📱 Sobre o projeto

O JB Motos é um aplicativo mobile para uma oficina/loja de motos, com login de usuário, cadastro de motos, agendamento de serviços e catálogo de peças/produtos.

## 🛠️ Tecnologias utilizadas

- **Linguagem:** Java
- **IDE:** Android Studio
- **Plataforma:** Android
- **Persistência:** Room (SQLite) e SharedPreferences (sessão do usuário via Gson)
- **Mapas:** Google Maps SDK

## ⚙️ Funcionalidades

- Login e gerenciamento de sessão do usuário
- Cadastro e remoção de motos do usuário
- Agendamento de serviços por moto (troca de óleo, pneus, revisão, etc.)
- Catálogo de produtos/peças (loja)
- Tela de contato com localização no mapa e ligação direta

## 🚀 Como executar

1. Clone este repositório:
   ```bash
   git clone https://github.com/seu-usuario/JBMotos.git
   ```
2. Abra o projeto no Android Studio.
3. Configure a chave da API do Google Maps (veja seção abaixo).
4. Aguarde a sincronização do Gradle.
5. Execute em um emulador ou dispositivo físico.

## 🔑 Configurando a API Key do Google Maps

Este projeto usa o Google Maps SDK na tela de contato, que exige uma API Key própria. Por segurança, a chave **não** fica no código — ela é lida do arquivo `local.properties`, que não é versionado no Git.

Para rodar o projeto localmente:

1. Crie (ou edite) o arquivo `local.properties` na raiz do projeto.
2. Adicione a linha:
   ```properties
   MAPS_API_KEY=sua_chave_aqui
   ```
3. Gere sua própria chave no [Google Cloud Console](https://console.cloud.google.com/) (ative a "Maps SDK for Android").

Sem essa chave configurada, o app compila normalmente, mas o mapa da tela de contato não será exibido.

## 📋 Requisitos

- Android Studio (versão recomendada: Flamingo ou superior)
- SDK Android: mínimo API 26 (Android 8.0), compilado com API 34
- JDK 8 ou superior

## 📄 Licença

Este projeto é de uso acadêmico.
