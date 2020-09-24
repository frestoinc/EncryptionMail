# EncryptionMail App
This simple app uses Exchange ActiveSync Protocol for email client/messaging. The app is able to send and read encrypted mail (.p7m) by signing and encrypting using ECC/RSA certs. The encryption and decryption uses certs obtain from an LDAP server and an external token(a card for example). You may change the how the encrption/decryption works by changing few parameters to read a soft cert for example.

## Sending:
- message formatted to S/MIME format
- byte message signed with your private ECC/RSA private key
- signedData packed into PKCS#7 format
- encrypt signedData with recipients public certs retrieved from LDAP
- encryptData packed into .p7m
- EAS sends email

## Reading: 
- unpacked .p7m to encryptedData
- decrypt with your private ECC/RSA private key
- verify signedData
- message formatted to plain text

## Tech Stack:
- Exchange ActiveSync Protocol
- Custom Bouncy Castle ECC and RSA signing and encryption
- Custom S/MIME mail
- UnboundId Ldap
- JSoup

## Android Tech Stack:
- MVVM
- AndroidX Jetpack
- Dagger2
- RxJava
- LiveData
- Room
- Retrofit/OkHttp3
- Glide
- Security Crypto
