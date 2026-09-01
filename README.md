# JB Motos

Aplicativo mobile desenvolvido no framework React Native, criado como projeto de faculdade.

## 📱 Sobre o projeto

O JB Motos é um aplicativo mobile para uma oficina de motos, com login diferenciado para clientes e administrador, cadastro de motos, agendamento de serviços e gerenciamento completo do catálogo de serviços pelo admin.

> **Nota:** o build deste projeto foi validado apenas para **Android**. O suporte a iOS não foi configurado (ex: a chave do Google Maps não está configurada para essa plataforma).

## 🛠️ Tecnologias utilizadas

- **Framework:** React Native
- **Navegação:** React Navigation (bottom tabs + stack)
- **Persistência:** SQLite (`react-native-sqlite-storage`) e AsyncStorage (sessão do usuário)
- **Mapas:** react-native-maps (Google Maps)

## ⚙️ Funcionalidades

- Login com dois perfis de usuário: **cliente** e **administrador**
- Cadastro de motos do cliente
- Agendamento de serviços por moto
- Catálogo de serviços da oficina
- Painel administrativo: adicionar, editar e gerenciar serviços
- Tela de contato com localização no mapa e ligação direta

## 🚀 Como executar

1. Clone este repositório:
   ```bash
   git clone https://github.com/lukasolivermsc/JBMotos
   ```
2. Instale as dependências:
   ```bash
   npm install
   ```
3. Configure a chave da API do Google Maps (veja seção abaixo).
4. Rode no Android:
   ```bash
   npm run android
   ```

## 🔑 Configurando a API Key do Google Maps

Este projeto usa o Google Maps na tela de contato, que exige uma API Key própria. Por segurança, a chave **não** fica no código — ela é lida do arquivo `local.properties` (dentro da pasta `android/`), que não é versionado no Git.

Para rodar o projeto localmente:

1. Crie (ou edite) o arquivo `android/local.properties`.
2. Adicione a linha:
   ```properties
   MAPS_API_KEY=sua_chave_aqui
   ```
3. Gere sua própria chave no [Google Cloud Console](https://console.cloud.google.com/) (ative a "Maps SDK for Android").

Sem essa chave configurada, o app compila normalmente, mas o mapa da tela de contato não será exibido.

## 📋 Requisitos

- Node.js 22 ou superior
- Android Studio (com SDK e emulador configurados)
- SDK Android: mínimo API 24 (Android 7.0), compilado com API 36
- JDK 17 ou superior (requisito do React Native 0.85)

## 📄 Licença

Este projeto é de uso acadêmico.
