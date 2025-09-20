#!/bin/bash
#
# SPDX-FileCopyrightText: 2023 Zextras <https://www.zextras.com>
#
# SPDX-License-Identifier: AGPL-3.0-only
#

OS=${1:-"ubuntu-jammy"}
tar czf package/carbonio-catalog-quarkus.tar.gz -C target/ quarkus-app
docker run -it \
  --entrypoint=yap \
  -v $(pwd)/artifacts:/artifacts \
  -v $(pwd):/project \
  -w /project \
  "docker.io/m0rf30/yap-${OS}:1.44" \
  build "${OS}" .