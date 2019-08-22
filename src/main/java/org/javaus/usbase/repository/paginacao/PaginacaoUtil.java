package org.javaus.usbase.repository.paginacao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * classe de paginação utilizada por todas as classes, a mesma deve estar declarada no
 * JPAConfig em ComponentScan para ser encotrada no sistema 
 * */
@Component  // @Component - Transforma a classe num componente spring, que pode ser injedado com Autowired 
public class PaginacaoUtil {

	public void preparar(Criteria criteria, Pageable pageable){
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primerioRegistro = paginaAtual * totalRegistrosPorPagina;  
		
		
		criteria.setFirstResult(primerioRegistro);
		criteria.setMaxResults(totalRegistrosPorPagina);
		
		// ordenacao de campos da tela
		Sort sort = pageable.getSort();
		// se for null e vazia a lista
		if(sort != null && sort.isSorted()){
			Sort.Order order = sort.iterator().next();
			String property = order.getProperty();
			criteria.addOrder(order.isAscending() ? Order.asc(property) : Order.desc(property));
		}
	}
}
