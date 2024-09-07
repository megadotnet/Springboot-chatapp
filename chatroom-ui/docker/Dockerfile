FROM registry.c2cloud.cn/ynfgs/nginx@sha256:67681478d781ad69afed4f6612481e11f346f6b2d5e71ea6e0a3851e40196f89
MAINTAINER peter
RUN rm /etc/nginx/conf.d/default.conf
ADD default.conf /etc/nginx/conf.d/
COPY . /usr/share/nginx/html/
