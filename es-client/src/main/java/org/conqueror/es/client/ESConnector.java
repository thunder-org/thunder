package org.conqueror.es.client;


import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class ESConnector {

	private static final Logger logger = LoggerFactory.getLogger(ESConnector.class);

	private final String pingTimeOutSec;
	private final String nodeSamplerIntervalSec;

	private TransportClient client = null;
	private TransportAddress[] addresses;
	private Settings settings;

	public ESConnector() {
		this(120, 5);
	}

	public ESConnector(int pingTimeOutSec, int nodeSamplerIntervalSec) {
		this.pingTimeOutSec = Integer.toString(pingTimeOutSec) + "s";
		this.nodeSamplerIntervalSec = Integer.toString(nodeSamplerIntervalSec) + "s";
	}

	public ESConnector open(String host, int port) {
		TransportAddress address;
		try {
			address = new TransportAddress(InetAddress.getByName(host), port);
		} catch (UnknownHostException e) {
			logger.error("ES host is unknown - {}:{}", host, port);
			return null;
		}
		Settings settings = Settings.builder()
				.put("client.transport.ping_timeout", pingTimeOutSec)
				.put("client.transport.nodes_sampler_interval", nodeSamplerIntervalSec)
				.build();

		return open(new TransportAddress[]{address}, settings);
	}

	public ESConnector open(String host, int port, String cluster) {
		TransportAddress address;
		try {
			address = new TransportAddress(InetAddress.getByName(host), port);
		} catch (UnknownHostException e) {
			logger.error("ES host is unknown - {}:{}", host, port);
			e.printStackTrace();
			return null;
		}

		return open(new TransportAddress[] { address }, cluster);
	}

	public ESConnector open(String[] addresses, String cluster) {
		TransportAddress[] inetSocketAddresses = new TransportAddress[addresses.length];

		for (int addrNum=0; addrNum<addresses.length; addrNum++) {
			String[] parts = addresses[addrNum].split(":");
			String host = parts[0];
			int port = Integer.parseInt(parts[1]);

			if (parts.length == 2) {
				try {
					inetSocketAddresses[addrNum] = new TransportAddress(InetAddress.getByName(host), port);
				} catch (UnknownHostException e) {
					logger.error("ES host is unknown - {}:{}", host, port);
					return null;
				}
			} else {
				logger.error("elasticsearch address format is wrong");

				inetSocketAddresses = null;
				break;
			}
		}

		return (inetSocketAddresses != null)? open(inetSocketAddresses, cluster) : null;
	}

	public ESConnector open(ESConnector connector) {
		return open(connector.getAddresses(), connector.getSettings());
	}

	public ESConnector open(InetSocketAddress[] addresses, String cluster) {
		return open(transformToTransportAddress(addresses), cluster);
	}

	public ESConnector open(TransportAddress[] addresses, String cluster) {
		Settings settings = Settings.builder()
				.put("cluster.name", cluster)
				.put("client.transport.ping_timeout", pingTimeOutSec)
				.put("client.transport.nodes_sampler_interval", nodeSamplerIntervalSec)
				.build();

		return open(addresses, settings);
	}

	public ESConnector open(InetSocketAddress[] addresses, Settings settings) {
		return open(transformToTransportAddress(addresses), settings);
	}

	public ESConnector open(TransportAddress[] addresses, Settings settings) {
		try {
			this.addresses = addresses;
			this.settings = settings;
			client = new PreBuiltTransportClient(settings).addTransportAddresses(addresses);
		} catch (Exception e) {
			logger.error("It's failed to make a client");
			return null;
		}

		return this;
	}

	public Client getClient() {
		return client;
	}

	public TransportAddress[] getAddresses() {
		return addresses;
	}

	public Settings getSettings() {
		return settings;
	}

	public void close() {
		if (client != null) client.close();
	}

	private TransportAddress[] transformToTransportAddress(InetSocketAddress[] addresses) {
		TransportAddress[] esAddresses = new TransportAddress[addresses.length];
		for (int addrIdx=0; addrIdx<addresses.length; addrIdx++) {
			esAddresses[addrIdx] = new TransportAddress(addresses[addrIdx]);
		}
		return esAddresses;
	}

}
