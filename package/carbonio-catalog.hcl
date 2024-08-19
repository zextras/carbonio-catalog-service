services {
  check {
    http     = "http://127.78.0.24:10000/health/"
    method   = "GET"
    timeout  = "1s"
    interval = "5s"
  }
  meta = {
    prom_port = "21501"
  }
  tags = [
    "prometheus-exporter"
  ]
  connect {
    sidecar_service {
      proxy {
        local_service_address = "127.78.0.24"
        expose {
          paths = [
            {
              path            = "/metrics",
              local_path_port = 10000
              listener_port   = 21501
            }
          ]
        }
        upstreams = [
          {
            destination_name   = "carbonio-mailbox-db"
            local_bind_port    = 20000
            local_bind_address = "127.78.0.24"
          },
          {
            destination_name   = "carbonio-mailbox"
            local_bind_port    = 20001
            local_bind_address = "127.78.0.24"
          },
          {
            destination_name   = "carbonio-mailbox-admin"
            local_bind_port    = 20002
            local_bind_address = "127.78.0.24"
          },
        ]
      }
    }
  }
  name = "carbonio-storages"
  port = 10000
}
