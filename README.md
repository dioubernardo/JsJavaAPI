# JsJavaAPI

## Como testar

1 - Instalar o certificado raiz, arquivo certificado/jsjavaapi-ca.crt, como "CA de Signatário" no gerenciador de certificados do Java, esse passo só é necessário se o certificado para assinar não for gerado por uma certificadora raiz
2 - Copiar os arquivos contidos na pasta test para servidor web e carregar o index.html

OBS: como o certificado foi gerado com a lista de revogação em http://localhost/revogados.crl copiar o arquivo certificado/revogados.crl para esse local ou recompilar e reassinar o jar alterando o endereço

## Como compilar

1 - Importar o projeto no Eclipse
2 - Fazer o build do projeto
3 - Com o certificado gerado executar via "Ant Build" o arquivo buildJar.xml

## Como gerar o certificado

1 - Garantir que o openssl está instalado
2 - Editar o arquivo certificado/criarCertificados.bat e alterar os dados necessários
3 - Executar o arquivo certificado/criarCertificados.bat

Obs: durante a criação uma senha é perguntada, essa senha deve ser igual a informada no atributo "storepass" do elemento "signjar" no arquivo "buildJar.xml"
