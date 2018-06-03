# Nautilus application server

Generate api spec:
`` 
bootprint openapi data-processing.yml src/main/resources/public/api
html-inline target/index.html
``

or

``
npx redoc-cli bundle ../../../../../data-processing.yml
npx redoc-cli bundle ../../../../../photos-processing.yml
``
