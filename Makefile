DOMAIN=demo.zextras.io
NODE:=$(shell echo $${NODE:-svcs})
VM_HOSTNAME=${HOST}-${NODE}.${DOMAIN}

clean:
	mvn clean

compile:
	mvn package -DskipTests

test:
	mvn package

run_dev:
	./run_dev.sh ${HOST} ${DOMAIN}

sys-status:
	ssh root@${VM_HOSTNAME} "systemctl status carbonio-catalog"

sys-stop:
	ssh root@${VM_HOSTNAME} "systemctl stop carbonio-catalog"

sys-start:
	ssh root@${VM_HOSTNAME} "systemctl start carbonio-catalog"

upload:
	ssh root@${VM_HOSTNAME} "rm -rf /usr/share/carbonio/carbonio-catalog/*"
	rsync -acz target/quarkus-app/* root@${VM_HOSTNAME}:/usr/share/carbonio/carbonio-catalog/

sys-install: clean compile
	./build_packages.sh
	./install_packages.sh ${VM_HOSTNAME}

sys-deploy: clean compile sys-stop upload sys-start

info:
	./service_info.sh -s ${SERVICE_ID} -h ${HOST}


.PHONY: clean compile test run_dev sys-status sys-stop sys-start upload sys-install sys-deploy