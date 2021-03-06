package org.javaus.usbase.repository.helper.cliente;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import org.javaus.usbase.model.Cliente;
import org.javaus.usbase.repository.filter.ClienteFilter;
import org.javaus.usbase.repository.paginacao.PaginacaoUtil;

public class ClientesImpl implements ClientesQueries {

	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cliente.class);
		
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		// .LEFT_OUTER_JOIN traz o retorno mesmo que codigo da cidade esteja null  
		criteria.createAlias("endereco.cidade","c", org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("c.estado","e", org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	
	}

	@Transactional(readOnly = true)
	@Override
	public Cliente buscaComEndereco(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cliente.class);
		criteria.createAlias("endereco.cidade","c", org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("c.estado","e", org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); // Agrupa por usuario e remove
		return (Cliente) criteria.uniqueResult();
	} 

	private Long total(ClienteFilter filtro){
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cliente.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
	
	private void adicionarFiltro(ClienteFilter filtro, Criteria criteria) {
		if(filtro != null){
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome().trim(), MatchMode.ANYWHERE));
			}
			
			if (!StringUtils.isEmpty(filtro.getCpfOuCnpj())) {
				criteria.add(Restrictions.eq("cpfOuCnpj", filtro.getCpfOuCnpjSemFormatacao()));
			}
		}
		
	}



}
