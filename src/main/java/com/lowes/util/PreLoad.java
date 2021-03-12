package com.lowes.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.lowes.service.BusquedaSolicitudService;

@Component
public class PreLoad implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private BusquedaSolicitudService busquedaSolicitudService;

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		System.out.println("Cargando Proveedores...");
    	busquedaSolicitudService.getProveedoresTodos();
	}
}