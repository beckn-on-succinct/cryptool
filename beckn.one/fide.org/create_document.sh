In CrypTool home run this.
--
payload=beckn.one/fide.org/document.json bin/mksignerstring /subjects/org/fide.org/documents post beckn.one/fide.org/kid.json 


curl -H 'AUTHORIZATION: Signature keyId="/subjects/org/fide.org/verification_methods/f412c7eb-8009-4f62-b09c-df0ba42d263c",algorithm="ed25519",created="1739210618",headers="(request-target) (created) digest",signature="1b55B7XHgC3VKNDNDBIdefPDKxDQdwxftSwwstOVyQUH3bQ+TKrfZy1KYeUtp1Wsfs7/uU3L0E5gZz85WQ3kDw=="' -H 'content-type:application/json' https://one.becknprotocol.io/subjects/org/fide.org/documents --data-binary @beckn.one/fide.org/document.json
