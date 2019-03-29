/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/
package com.mangosteen.jacoco.report;

import com.mangosteen.jacoco.core.analysis.IBundleCoverage;
import com.mangosteen.jacoco.core.data.ExecutionData;
import com.mangosteen.jacoco.core.data.SessionInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A report visitor that is composed from multiple other visitors. This can be
 * used to create more than one report format in one run.
 */
public class MultiReportVisitor extends MultiGroupVisitor implements
        IReportVisitor {

	private final List<IReportVisitor> visitors;

	/**
	 * New visitor delegating to all given visitors.
	 *
	 * @param visitors
	 *            visitors to delegate to
	 */
	public MultiReportVisitor(final List<IReportVisitor> visitors) {
		super(visitors);
		this.visitors = visitors;
	}

	public void visitInfo(final List<SessionInfo> sessionInfos,
			final Collection<ExecutionData> executionData) throws IOException {
		for (final IReportVisitor v : visitors) {
			v.visitInfo(sessionInfos, executionData);
		}
	}

	public void visitEnd() throws IOException {
		for (final IReportVisitor v : visitors) {
			v.visitEnd();
		}
	}

}

class MultiGroupVisitor implements IReportGroupVisitor {

	private final List<? extends IReportGroupVisitor> visitors;

	MultiGroupVisitor(final List<? extends IReportGroupVisitor> visitors) {
		this.visitors = visitors;
	}

	public void visitBundle(final IBundleCoverage bundle,
			final ISourceFileLocator locator) throws IOException {
		for (final IReportGroupVisitor v : visitors) {
			v.visitBundle(bundle, locator);
		}
	}

	public IReportGroupVisitor visitGroup(final String name) throws IOException {
		final List<IReportGroupVisitor> children = new ArrayList<IReportGroupVisitor>();
		for (final IReportGroupVisitor v : visitors) {
			children.add(v.visitGroup(name));
		}
		return new MultiGroupVisitor(children);
	}

}