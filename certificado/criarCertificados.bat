@echo off

rem http://blog.didierstevens.com/2013/05/08/howto-make-your-own-cert-and-revocation-list-with-openssl/

set prefixFileName=jsjavaapi
set URIRevogation=http://localhost/revogados.crl
set subj="/C=BR/CN=Bernardo Silva/OU=Bernardo Silva/O=Bernardo Silva"

mkdir tmp
cd tmp

echo|set /p= > cert.index
echo|set /p=01 > cert.serial
echo|set /p=01 > cert.crlnumber

rem Mainly copied from:
rem http://swearingscience.com/2009/01/18/openssl-self-signed-ca/
(
echo [ ca ]
echo default_ca = myca
echo [ crl_ext ]
echo authorityKeyIdentifier=keyid:always
echo [ myca ]
echo dir = .
echo new_certs_dir = $dir
echo unique_subject = no
echo certificate = $dir/%prefixFileName%-ca.crt
echo database = $dir/cert.index
echo private_key = $dir/%prefixFileName%-ca.key
echo serial = $dir/cert.serial
echo default_days = 730
echo default_md = sha1
echo policy = myca_policy
echo x509_extensions = myca_extensions
echo crlnumber = $dir/cert.crlnumber
echo default_crl_days = 730
echo [ myca_policy ]
echo commonName = supplied
echo stateOrProvinceName = optional
echo countryName = supplied
echo emailAddress = optional
echo organizationName = supplied
echo organizationalUnitName = optional
echo [ myca_extensions ]
echo basicConstraints = CA:false
echo subjectKeyIdentifier = hash
echo authorityKeyIdentifier = keyid:always
echo keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment, keyAgreement, keyCertSign, cRLSign, encipherOnly, decipherOnly
echo extendedKeyUsage = serverAuth, clientAuth, codeSigning, emailProtection, timeStamping, msCodeInd, msCodeCom, msCTLSign, msSGC, msEFS, nsSGC
echo crlDistributionPoints = URI:%URIRevogation%
) > ca.conf

openssl genrsa -out %prefixFileName%-ca.key 4096
openssl req -new -x509 -key %prefixFileName%-ca.key -out %prefixFileName%-ca.crt -days 3650 -subj %subj%

openssl genrsa -out %prefixFileName%.key 4096
openssl req -new -key %prefixFileName%.key -out %prefixFileName%.csr -subj %subj%

openssl ca -batch -config ca.conf -notext -in %prefixFileName%.csr -out %prefixFileName%.crt -days 3650
openssl pkcs12 -export -out %prefixFileName%.p12 -inkey %prefixFileName%.key -in %prefixFileName%.crt -chain -CAfile %prefixFileName%-ca.crt

rem estrutura de revogação
openssl ca -config ca.conf -gencrl -keyfile %prefixFileName%-ca.key -cert %prefixFileName%-ca.crt -out revogados.crl.key
openssl crl -inform PEM -in revogados.crl.key -outform DER -out revogados.crl

copy %prefixFileName%-ca.crt ..\
copy %prefixFileName%-ca.csr ..\
copy %prefixFileName%.p12 ..\
copy revogados.crl ..\

cd ..

del /q /s tmp
