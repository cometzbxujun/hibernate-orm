/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.metamodel.source.internal.hbm;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.mapping.PropertyGeneration;
import org.hibernate.metamodel.reflite.spi.JavaTypeDescriptor;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbColumnElement;
import org.hibernate.metamodel.source.internal.jaxb.hbm.JaxbKeyPropertyElement;
import org.hibernate.metamodel.source.spi.HibernateTypeSource;
import org.hibernate.metamodel.source.spi.RelationalValueSource;
import org.hibernate.metamodel.source.spi.SingularAttributeSource;
import org.hibernate.metamodel.source.spi.SizeSource;
import org.hibernate.metamodel.source.spi.ToolingHintSource;
import org.hibernate.metamodel.spi.binding.SingularAttributeBinding;

/**
 * Implementation for {@code <key-property/>} mappings
 *
 * @author Gail Badner
 */
class KeyAttributeSourceImpl
		extends AbstractHbmSourceNode
		implements SingularAttributeSource {
	private final JaxbKeyPropertyElement keyPropertyElement;
	private final SingularAttributeBinding.NaturalIdMutability naturalIdMutability;
	private final HibernateTypeSource typeSource;
	private final List<RelationalValueSource> valueSources;

	public KeyAttributeSourceImpl(
			MappingDocument mappingDocument,
			final JaxbKeyPropertyElement keyPropertyElement,
			final SingularAttributeBinding.NaturalIdMutability naturalIdMutability) {
		super( mappingDocument );
		this.keyPropertyElement = keyPropertyElement;
		this.naturalIdMutability = naturalIdMutability;
		this.typeSource = new HibernateTypeSource() {
			private final String name = keyPropertyElement.getTypeAttribute() != null
					? keyPropertyElement.getTypeAttribute()
					: keyPropertyElement.getType() != null
					? keyPropertyElement.getType().getName()
					: null;
			private final Map<String, String> parameters = ( keyPropertyElement.getType() != null )
					? Helper.extractParameters( keyPropertyElement.getType().getParam() )
					: null;

			@Override
			public String getName() {
				return name;
			}

			@Override
			public Map<String, String> getParameters() {
				return parameters;
			}
			@Override
			public JavaTypeDescriptor getJavaType() {
				return null;
			}
		};
		this.valueSources = Helper.buildValueSources(
				sourceMappingDocument(),
				new Helper.ValueSourcesAdapter() {
					@Override
					public String getColumnAttribute() {
						return keyPropertyElement.getColumnAttribute();
					}

					@Override
					public SizeSource getSizeSource() {
						return Helper.createSizeSourceIfMapped(
								keyPropertyElement.getLength(),
								null,
								null
						);
					}


					@Override
					public List<JaxbColumnElement> getColumn() {
						return keyPropertyElement.getColumn();
					}

					@Override
					public boolean isIncludedInInsertByDefault() {
						return true;
					}

					@Override
					public boolean isForceNotNull() {
						return true;
					}
				}
		);
	}

	@Override
	public String getName() {
		return keyPropertyElement.getName();
	}

	@Override
	public HibernateTypeSource getTypeInformation() {
		return typeSource;
	}

	@Override
	public String getPropertyAccessorName() {
		return keyPropertyElement.getAccess();
	}

	@Override
	public PropertyGeneration getGeneration() {
		return PropertyGeneration.NEVER;
	}

	@Override
	public boolean isLazy() {
		return false;
	}

	@Override
	public SingularAttributeBinding.NaturalIdMutability getNaturalIdMutability() {
		return naturalIdMutability;
	}

	@Override
	public boolean isIncludedInOptimisticLocking() {
		return false;
	}

	@Override
	public Nature getNature() {
		return Nature.BASIC;
	}

	@Override
	public boolean isVirtualAttribute() {
		return false;
	}

	@Override
	public boolean areValuesIncludedInInsertByDefault() {
		return true;
	}

	@Override
	public boolean areValuesIncludedInUpdateByDefault() {
		return true;
	}

	@Override
	public boolean areValuesNullableByDefault() {
		return false;
	}

	@Override
	public String getContainingTableName() {
		return null;
	}

	@Override
	public List<RelationalValueSource> relationalValueSources() {
		return valueSources;
	}

	@Override
	public boolean isSingular() {
		return true;
	}

	@Override
	public Collection<? extends ToolingHintSource> getToolingHintSources() {
		return keyPropertyElement.getMeta();
	}
}
