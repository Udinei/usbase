package org.javaus.usbase.repository.helper.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import org.javaus.usbase.model.Grupo;
import org.javaus.usbase.model.Usuario;
import org.javaus.usbase.model.UsuarioGrupo;
import org.javaus.usbase.repository.filter.UsuarioFilter;
import org.javaus.usbase.repository.paginacao.PaginacaoUtil;

public class UsuariosImpl implements UsuariosQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;


	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		
		//criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		List<Usuario> filtrados = criteria.list(); // retorna o resultado do criteria somente usuarios
		filtrados.forEach(u -> Hibernate.initialize(u.getGrupos())); // inicializa os grupos de cada usuario individualmente
		
		return new PageImpl<>(filtrados, pageable, total(filtro));
	}

	
	@Transactional(readOnly = true)  // readOnly informa que é somente pesquisa
	@Override
	public Usuario buscarComGrupos(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN); // Fazendo relacionamento(Eager) com a tabela usuario e trazendo os grupos
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); // Agrupa por Usuario.class (DISTINCT_ROOT_ENTITY) e removendo os repetidos
		return (Usuario) criteria.uniqueResult();
	}
	

	private Long total(UsuarioFilter filtro){
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
	
	private void adicionarFiltro(UsuarioFilter filtro, Criteria criteria) {
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			if (!StringUtils.isEmpty(filtro.getEmail())) {
				criteria.add(Restrictions.ilike("email", filtro.getEmail(), MatchMode.START));
			}
						
			if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
				
				// lista que funcionara como a clasula and na consulta sql
				List<Criterion> subqueries = new ArrayList<>();
				
				// retorna para codigoGrupo o codiogo de cada grupo selecionado na tela
				for (Long codigoGrupo : filtro.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
					
					// DetachedCriteria - faz uma consulta separada um "subselect" da criteria principal para a classe UsuarioGrupo
					DetachedCriteria dc = DetachedCriteria.forClass(UsuarioGrupo.class);
                        					
						dc.add(Restrictions.eq("id.grupo.codigo", codigoGrupo));  // filtra pelo codigo do grupo
						dc.setProjection(Projections.property("id.usuario"));     // Projections - retorna o codigo do usuario
					
						subqueries.add(Subqueries.propertyIn("codigo", dc));      // a cada loop retorna o resultado do subselect   
						                                                          // u.codigo in (codigo) e adiciona na lista de subqueries      
				}
				
				// cria um array Criterion, do tamanho da lista de subqueries
				Criterion[] criterions = new Criterion[subqueries.size()];

				// Transforma subqueries em arrays de Criterions. 
				// aplica and nos resultados da subqueries
				// passando o resultado das subqueries para a queries principal
				criteria.add(Restrictions.and(subqueries.toArray(criterions))); 
				
			}
		}
	}

	
	
	// carrega usuario do BD para autenticacao
	@Override
	public Optional<Usuario> porEmailEAtivo(String email) {
		// consulta usando JPQL
		return manager
				.createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
				.setParameter("email", email).getResultList().stream().findFirst(); // .stream().findFirst() 
		                                                                            // permite retornar um optional em vez de getResultList que retorna  um usuario
		
	}
	

	// carrega permissoes do usuario
	@Override
	public List<String> permissoes(Usuario usuario) {

		// :usuario - se o parametro for um objeto ele sempre vai pegar o id do objeto, para passar na consulta
		return manager.createQuery(
				"select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario", String.class)
				.setParameter("usuario", usuario)
				.getResultList();
					
	}



	
}
