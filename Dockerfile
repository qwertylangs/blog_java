FROM ubuntu:latest
LABEL authors="volko"

ENTRYPOINT ["top", "-b"]