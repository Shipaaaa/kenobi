# Image-processing

Модуль обработки фотографий.

## Usage

1) Собираем jar

```shell
../gradlew :image-processing:build 
````

2) Копируем `image-processing-1.0-SNAPSHOT-all.jar` и `Dockerfile` для нужной версии spark на сервер в папку `/opt/spark`


3) Собираем докер образ и публикуем на <http://hub.docker.com/>.

```shell
bin/docker-image-tool.sh -r shipa -t latest -f ./Dockerfile build
bin/docker-image-tool.sh -r shipa -t latest -f ./Dockerfile push
```

## Решение проблем

### Если docker build возвращает ошибку

```shell
sudo systemctl restart firewalld.service
```
