Descrição do problema: 

Objetivo: Criar uma API para Busca de Voos.

Tecnologias:
- A API Rest criada utilizando Java 8, Maven e SpringBoot;
- Captura de Dados CSV e JSON utilizando a bibliotecas (org.apache.commons-csv) e (com.googlecode.json-simple)
- Documentação de projeto em Swagger (springfox-swagger2)

Executando a aplicação:
- Baixar o projeto com o comando "git clone git@github.com:paulocorreaslz/tegra-api.git"

Entrar no diretorio "tegra-api/target" e executar o comando:
- java -jar tegra-api.war

Importando no Eclipse:
- Abrir o eclipse, clicar no menu "File", depois no sub-menu "Import". Selecionar o tipo de projeto "Maven" e em seguida escolher "Existing Maven Project". Clicar em "next", depois localize o diretorio onde o projeto foi clonado, para o eclipse localizar o arquivo "pom.xml". Apos escolher o diretório deixe marcado o checkbox ao lado do arquivo "pom.xml", logo após, clique em "finish" para o eclipse e o maven configurar o projeto e baixar as dependencias do mesmo.

Executando dentro do eclipse:
- Com o projeto clonado clique com o botão direito do mouse na classe "TegraApplication.java" que está localizada em "Java Resources" > "src/main/java/", dentro do pacote "com.paulocorreaslz.tegra" e localize o menu "Run as", e seguida clique a opção "java application" para o eclpise executar a aplicação.

Acessando a api:
- Digite no navegador o endereço "http://localhost:9090/api/online" para testar a aplicação, após o comando deve ser mostrada na tela a mensagem "online".

Acessando o Swagger:
- digite no navegador o endereço "http://localhost:9090/swagger-ui.html" para acessar o swagger e ver os serviços disponiveis da api

Urls disponíveis:
- Para verificar se a aplicação esta online digite no navegador o endereço "http://localhost:9090/api/online"
- Para listar os aeroportos digite no navegador o endereço "http://localhost:9090/api/airports"
- Para listar os trechos de voo da operadora uber digite no navegador o endereço "http://localhost:9090/api/uber"
- Para listar os trechos de voo da operadora 99planes digite no navegador o endereço "http://localhost:9090/api/planes"
- Para listar os trechso das duas operadoras digite no navegador o endereço "http://localhost:9090/api/all"
- Para consultar um voo pela origem, destino e data a urel segue o padrao a seguir "http://localhost:9090/api/search/{ORIGEM}/{DESTINO}/{DATA}", onde no lugar de {ORIGEM} deve ser informado as 3 Letras do aeroporto de origem, exemplo: PLU. No lugar de {DESTINO} deve ser informado as 3 letras do aeroporto de destino, exemplo: VCP e no lugar de {DATA} deve ser informada a data no padrao ANO-MES-DIA, exemplo 2019-02-12. Exemplo de url para consultar: "http://localhost:9090/api/search/PLU/FLN/2019-02-11"
  

Docker-Compose (docker)
- dentro do diretório do projeto utilize o comando "docker-compose up -d"

