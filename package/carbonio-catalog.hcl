services {
  check {
    http     = "http://127.78.0.28:10000/health/"
    method   = "GET"
    timeout  = "1s"
    interval = "5s"
  }
  meta = {
    prom_port = "21512"
  }
  tags = [
    "prometheus-exporter"
  ]
  connect {
    sidecar_service {
      proxy {
        local_service_address = "127.78.0.28"
        expose {
          paths = [
            {
              path            = "/metrics",
              local_path_port = 10000
              listener_port   = 21512
            }
          ]
        }
        upstreams = [
        ]
      }
    }
  }
  name = "carbonio-catalog"
  port = 10000
}
