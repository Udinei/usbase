package org.javaus.usbase.repository.helper.cidade;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import org.javaus.usbase.model.Cidade;
import org.javaus.usbase.repository.filter.CidadeFilter;
import org.javaus.usbase.repository.paginacao.PaginacaoUtil;

public class CidadesImpl implements CidadesQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);

		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		criteria.createAlias("estado", "e");

		return new PageImpl<>(criteria.list(), pageable, total(filtro));

	}
	
	
	
	@Transactional(readOnly = true)
	@Override
	public Cidade buscarComEstados(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		criteria.createAlias("estado", "e", JoinType.LEFT_OUTER_JOIN); // Fazendo relacionamento com a tabela grupos
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); // Agrupa por usuario e remove
		return (Cidade) criteria.uniqueResult();
	}

	private Long total(CidadeFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(CidadeFilter filtro, Criteria criteria) {
		if (filtro != null) {
			
			if (filtro.getEstado() != null) {
				criteria.add(Restrictions.eq("estado", filtro.getEstado()));
			}
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}

			
		}

	}

	


}
